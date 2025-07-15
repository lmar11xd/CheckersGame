package com.lmar.checkersgame.domain.usecase.single_game

import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.repository.IGameRepository
import javax.inject.Inject

class FinishGameUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(game: Game, winnerId: String) {
        val finishedGame = game.copy(
            winner = winnerId,
            status = GameStatusEnum.FINISHED,
            updatedAt = System.currentTimeMillis()
        )
        gameRepository.createSingleGame(finishedGame)
    }
}
