package com.lmar.checkersgame.domain.usecase.game

import com.lmar.checkersgame.core.utils.Constants
import com.lmar.checkersgame.data.sound.SoundPlayerWrapper
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.domain.util.requirePlayerIds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class EndGameUseCase(
    private val repository: IGameRepository,
    private val userRepository: IUserRepository,
    private val scope: CoroutineScope,
    private val gameId: String,
    private val soundPlayer: SoundPlayerWrapper
) {
    fun execute(game: Game, winnerId: String, onRematchReady: () -> Unit) {
        val (player1Id, player2Id) = game.requirePlayerIds()

        if (winnerId == player1Id) soundPlayer.playWin() else soundPlayer.playLose()

        scope.launch {
            repository.setWinner(gameId, winnerId)
            repository.setGameStatus(gameId, GameStatusEnum.FINISHED)

            // Guardar puntajes finales
            repository.setFinalScores(gameId, game.scores)

            // Actualizar score del jugador en su perfil
            userRepository.updateUserScore(winnerId, Constants.POINTS_MATCH_WIN)

            // Escuchar revancha
            repository.listenToRematchRequests(gameId, player1Id, player2Id) {
                onRematchReady()
            }
        }
    }
}