package com.lmar.checkersgame.data.repository

import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.Player

interface IGameRepository {
    fun listenToGame(gameId: String, onUpdate: (Game) -> Unit)
    suspend fun updateBoard(gameId: String, board: List<List<Piece>>)
    suspend fun updateTurn(gameId: String, turn: String)
    suspend fun createOrJoinGame(player: Player, roomId: String): String
    suspend fun setGameStatus(gameId: String, status: GameStatusEnum)
    suspend fun setWinner(gameId: String, winnerId: String)
}