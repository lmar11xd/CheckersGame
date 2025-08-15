package com.lmar.checkersgame.domain.usecase.single_game

import com.lmar.checkersgame.domain.logic.hasAnyValidMoves
import com.lmar.checkersgame.domain.logic.hasPieces
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.util.requirePlayerIds
import javax.inject.Inject

class CheckWinnerUseCase @Inject constructor(
    private val finishGameUseCase: FinishGameUseCase
) {
    suspend operator fun invoke(
        board: List<MutableList<Piece>>,
        game: Game,
        isAuthenticated: Boolean
    ): String? {
        val (p1, p2) = game.requirePlayerIds()

        val aiHasPieces = hasPieces(board, p2)
        val aiCanMove = hasAnyValidMoves(board, p2, p1, p2)
        val userHasPieces = hasPieces(board, p1)
        val userCanMove = hasAnyValidMoves(board, p1, p1, p2)

        return when {
            !aiHasPieces || !aiCanMove -> {
                if (isAuthenticated) {
                    finishGameUseCase(game, p1)
                }
                p1
            }
            !userHasPieces || !userCanMove -> {
                if (isAuthenticated) {
                    finishGameUseCase(game, p2)
                }
                p2
            }
            else -> null
        }
    }
}