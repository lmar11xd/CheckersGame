package com.lmar.checkersgame.domain.usecase.single_game

import com.lmar.checkersgame.domain.enum.GameStatusEnum
import com.lmar.checkersgame.domain.logic.generateInitialBoard
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Player
import javax.inject.Inject

class ResetSingleGameUseCase @Inject constructor() {
    operator fun invoke(userId: String): Game {
        val player1 = Player(userId, "Tú")
        val player2 = Player("AI", "IA")
        val initialBoard = generateInitialBoard(player1.id, player2.id)
        return Game(
            board = initialBoard,
            player1 = player1,
            player2 = player2,
            turn = player1.id,
            status = GameStatusEnum.PLAYING
        )
    }
}