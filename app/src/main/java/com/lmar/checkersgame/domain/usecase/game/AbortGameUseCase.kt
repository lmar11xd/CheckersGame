package com.lmar.checkersgame.domain.usecase.game

import com.lmar.checkersgame.core.utils.Constants
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.repository.IRoomRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.domain.util.requirePlayerIds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AbortGameUseCase(
    private val repository: IGameRepository,
    private val roomRepository: IRoomRepository,
    private val userRepository: IUserRepository,
    private val scope: CoroutineScope,
    private val gameId: String,
    private val roomId: String
) {
    fun execute(game: Game, userId: String) {
        val (player1Id, player2Id) = game.requirePlayerIds()
        val opponentId = if (userId == player1Id) player2Id else player1Id

        scope.launch {
            // Actualizar score del jugador en su perfil
            val pointsEarned = Constants.POINTS_ABORTED_MATCH
            val winnerUser = userRepository.getUserById(userId)
            if (winnerUser != null) {
                val updatedScore = winnerUser.score - pointsEarned // Restamos los puntos por salir del juego
                userRepository.updateUserScore(userId, updatedScore)
            }

            repository.setWinner(gameId, opponentId)
            repository.setGameStatus(gameId, GameStatusEnum.ABORTED)
            roomRepository.setRoomStatus(roomId, RoomStatusEnum.CLOSED)
        }
    }
}