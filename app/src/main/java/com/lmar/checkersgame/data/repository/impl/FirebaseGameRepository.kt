package com.lmar.checkersgame.data.repository.impl

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lmar.checkersgame.data.repository.IGameRepository
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.logic.generateInitialBoard
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseGameRepository @Inject constructor() : IGameRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("${Constants.DATABASE_REFERENCE}/${Constants.GAMES_REFERENCE}")

    override fun listenToGame(gameId: String, onUpdate: (Game) -> Unit) {
        database.child(gameId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(Game::class.java)?.let { onUpdate(it) }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override suspend fun updateBoard(gameId: String, board: List<List<Piece>>) {
        database.child(gameId).child("board").setValue(board)
    }

    override suspend fun updateTurn(gameId: String, turn: String) {
        database.child(gameId).child("turn").setValue(turn)
    }

    override suspend fun createOrJoinGame(player: Player, roomId: String): String {
        val gamesRef = database
        val query = gamesRef.orderByChild("roomId").equalTo(roomId)
        val snapshot = query.get().await()

        for (gameSnapshot in snapshot.children) {
            val game = gameSnapshot.getValue(Game::class.java)

            if (game != null &&
                game.player1 != null &&
                game.status == GameStatusEnum.WAITING &&
                game.player1.id != player.id
            ) {
                val gameId = gameSnapshot.key ?: continue
                val player1Id = game.player1.id

                val newBoard = generateInitialBoard(player1Id, player.id)

                gamesRef.child(gameId).child("player2").setValue(player).await()
                gamesRef.child(gameId).child("status").setValue(GameStatusEnum.PLAYING).await()
                gamesRef.child(gameId).child("board").setValue(newBoard).await()
                gamesRef.child(gameId).child("updatedAt").setValue(System.currentTimeMillis()).await()

                return gameId
            }
        }

        // Si no hay partidas disponibles, crear una nueva
        val newGameId = UUID.randomUUID().toString()
        val initialBoard = generateInitialBoard(player.id)
        val currentTimestamp = System.currentTimeMillis()

        val newGame = Game(
            roomId = roomId,
            player1 = player,
            board = initialBoard,
            turn = player.id,
            status = GameStatusEnum.WAITING,
            createdAt = currentTimestamp,
            updatedAt = currentTimestamp
        )

        Log.d("RealtimeGameRepository", "Creating new game with ID: $newGameId")
        gamesRef.child(newGameId).setValue(newGame).await()

        return newGameId
    }

    override suspend fun setGameStatus(gameId: String, status: GameStatusEnum) {
        database.child(gameId).child("status").setValue(status)
    }

    override suspend fun setWinner(gameId: String, winnerId: String) {
        database.child(gameId).child("winner").setValue(winnerId)
    }
}