package com.lmar.checkersgame.domain.usecase.single_game

import com.lmar.checkersgame.domain.ai.AIPlayer
import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.logic.generateInitialBoard
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Player
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import javax.inject.Inject

class InitializeSingleGameUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    operator fun invoke(
        difficulty: String,
        onResult: (Game, String, AIPlayer) -> Unit
    ) {
        val level = com.lmar.checkersgame.domain.ai.Difficulty.valueOf(difficulty)
        val aiPlayer = AIPlayer(level)
        val userId = authRepository.getCurrentUser()?.uid.orEmpty()
        val player1 = Player(userId, "Tú")
        val player2 = Player("AI", "IA")
        val board = generateInitialBoard(player1.id, player2.id)
        val game = Game(
            board = board,
            player1 = player1,
            player2 = player2,
            turn = player1.id,
            status = GameStatusEnum.PLAYING,
            level = level
        )
        onResult(game, userId, aiPlayer)
    }
}