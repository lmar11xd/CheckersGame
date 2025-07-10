package com.lmar.checkersgame.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.logic.generateInitialBoard
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.core.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.get

class FirebaseGameRepository @Inject constructor() : IGameRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance()
        .getReference("${Constants.DATABASE_REFERENCE}/${Constants.GAMES_REFERENCE}")

    override fun listenToGame(gameId: String, onUpdate: (Game) -> Unit) {
        database.child(gameId).addValueEventListener(object : ValueEventListener {
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

            if (game != null && game.player1 != null && game.status == GameStatusEnum.WAITING && game.player1.id != player.id) {
                val gameId = gameSnapshot.key ?: continue
                val player1Id = game.player1.id

                val newBoard = generateInitialBoard(player1Id, player.id)
                val randomTurn = arrayOf(player1Id, player.id).random()

                gamesRef.child(gameId).child("player2").setValue(player).await()
                gamesRef.child(gameId).child("status").setValue(GameStatusEnum.PLAYING).await()
                gamesRef.child(gameId).child("board").setValue(newBoard).await()
                gamesRef.child(gameId).child("turn").setValue(randomTurn).await()
                gamesRef.child(gameId).child("updatedAt").setValue(System.currentTimeMillis())
                    .await()

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

        gamesRef.child(newGameId).setValue(newGame).await()

        return newGameId
    }

    override suspend fun createGame(
        player1: Player,
        player2: Player,
        roomId: String
    ): String {
        val newGameId = UUID.randomUUID().toString()
        val initialBoard = generateInitialBoard(player1.id)
        val currentTimestamp = System.currentTimeMillis()

        val randomTurn = arrayOf(player1.id, player2.id).random()

        val newGame = Game(
            roomId = roomId,
            player1 = player1,
            player2 = player2,
            board = initialBoard,
            turn = randomTurn,
            status = GameStatusEnum.PLAYING,
            createdAt = currentTimestamp,
            updatedAt = currentTimestamp
        )

        database.child(newGameId).setValue(newGame).await()

        return newGameId
    }

    override suspend fun createSingleGame(game: Game): String {
        val newGameId = UUID.randomUUID().toString()
        database.child(newGameId).setValue(game).await()
        return newGameId
    }

    override suspend fun setGameStatus(gameId: String, status: GameStatusEnum) {
        database.child(gameId).child("status").setValue(status)
    }

    override suspend fun setWinner(gameId: String, winnerId: String) {
        database.child(gameId).child("winner").setValue(winnerId)
    }

    override suspend fun requestRematch(gameId: String, userId: String) {
        database.child(gameId).child("rematchRequests").child(userId).setValue(true)
    }

    override suspend fun listenToRematchRequests(
        gameId: String, player1Id: String, player2Id: String, onBothAccepted: () -> Unit
    ) {
        database.child(gameId).child("rematchRequests")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = snapshot.value as? Map<*, *> ?: return
                    val player1Accepted = map[player1Id] == true
                    val player2Accepted = map[player2Id] == true

                    if (player1Accepted && player2Accepted) {
                        onBothAccepted()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override suspend fun clearRematchRequests(gameId: String) {
        database.child(gameId).child("rematchRequests").removeValue()
    }
}