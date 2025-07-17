package com.lmar.checkersgame.domain.usecase.single_game

import com.lmar.checkersgame.core.utils.Constants
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import javax.inject.Inject

class FinishGameUseCase @Inject constructor(
    private val gameRepository: IGameRepository,
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(game: Game, winnerId: String) {
        val finishedGame = game.copy(
            winner = winnerId,
            status = GameStatusEnum.FINISHED,
            updatedAt = System.currentTimeMillis()
        )
        gameRepository.createSingleGame(finishedGame)
        userRepository.updateUserScore(winnerId, Constants.POINTS_MATCH_WIN)
    }
}
