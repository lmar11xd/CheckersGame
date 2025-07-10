package com.lmar.checkersgame.domain.repository

import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.Player

interface IGameRepository {
    fun listenToGame(gameId: String, onUpdate: (Game) -> Unit)
    suspend fun updateBoard(gameId: String, board: List<List<Piece>>)
    suspend fun updateTurn(gameId: String, turn: String)
    suspend fun createOrJoinGame(player: Player, roomId: String): String
    suspend fun createGame(player1: Player, player2: Player, roomId: String): String
    suspend fun createSingleGame(game: Game): String
    suspend fun setGameStatus(gameId: String, status: GameStatusEnum)
    suspend fun setWinner(gameId: String, winnerId: String)
    suspend fun requestRematch(gameId: String, userId: String)
    suspend fun listenToRematchRequests(gameId: String, player1Id: String, player2Id: String, onBothAccepted: () -> Unit)
    suspend fun clearRematchRequests(gameId: String)
}