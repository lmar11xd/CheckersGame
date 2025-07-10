package com.lmar.checkersgame.domain.ai

import com.lmar.checkersgame.domain.logic.*
import com.lmar.checkersgame.domain.model.Piece

class AIPlayer(private val difficulty: Difficulty) {

    fun getNextMove(
        board: List<MutableList<Piece>>,
        aiId: String,
        opponentId: String
    ): Pair<Position, Position>? {
        val allMoves = mutableListOf<Pair<Position, Position>>()

        for (row in board.indices) {
            for (col in board[row].indices) {
                val piece = board[row][col]
                if (piece.playerId == aiId) {
                    val from = row to col
                    for (dr in listOf(-1, 1)) {
                        for (dc in listOf(-1, 1)) {
                            val to = (row + dr) to (col + dc)
                            if (to.first in board.indices && to.second in board[to.first].indices) {
                                if (isValidMove(board, from, to, aiId, opponentId, aiId)) {
                                    allMoves.add(from to to)
                                }
                            }
                            val toJump = (row + 2 * dr) to (col + 2 * dc)
                            if (toJump.first in board.indices && toJump.second in board[toJump.first].indices) {
                                if (isValidMove(board, from, toJump, aiId, opponentId, aiId)) {
                                    allMoves.add(from to toJump)
                                }
                            }
                        }
                    }
                }
            }
        }

        return when (difficulty) {
            Difficulty.EASY -> allMoves.randomOrNull()
            Difficulty.MEDIUM -> allMoves.find { isJumpMove(it) } ?: allMoves.randomOrNull()
            Difficulty.HARD -> allMoves.maxByOrNull { evaluateMove(it, board, aiId, opponentId) }
        }
    }

    private fun isJumpMove(move: Pair<Position, Position>): Boolean {
        val (from, to) = move
        val rowDiff = kotlin.math.abs(to.first - from.first)
        val colDiff = kotlin.math.abs(to.second - from.second)
        return rowDiff == 2 && colDiff == 2
    }

    private fun evaluateMove(
        move: Pair<Position, Position>,
        board: List<MutableList<Piece>>,
        aiId: String,
        opponentId: String
    ): Int {
        val (from, to) = move
        var score = 0

        // Recompensa por salto (captura)
        if (isJumpMove(move)) score += 5

        // Recompensa por llegar a ser rey
        val piece = board[from.first][from.second]
        val wouldBecomeKing = !piece.isKing &&
                shouldBeKing(piece, to.first, opponentId, aiId)
        if (wouldBecomeKing) score += 3

        return score
    }
}

enum class Difficulty(val value: String) {
    EASY("Fácil"), MEDIUM("Normal"), HARD("Difícil")
}

