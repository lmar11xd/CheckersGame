package com.lmar.checkersgame.domain.usecase.single_game

import com.lmar.checkersgame.domain.ai.AIPlayer
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.logic.canContinueJumping
import com.lmar.checkersgame.domain.logic.shouldBeKing
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.sound.ISoundPlayer
import com.lmar.checkersgame.domain.util.requirePlayerIds
import kotlinx.coroutines.delay
import javax.inject.Inject

class MakeAIMoveUseCase @Inject constructor(
    private val soundPlayer: ISoundPlayer
) {
    suspend operator fun invoke(
        game: Game,
        updateGame: (Game) -> Unit
    ) {
        val ai = AIPlayer(game.level)
        val (p1, p2) = game.requirePlayerIds()
        var board = game.board.map { it.toMutableList() }
        var move: Pair<Position, Position>?

        do {
            delay(500)
            move = ai.getNextMove(board, p2, p1) ?: break
            val (from, to) = move
            board = board.map { it.map { it.copy() }.toMutableList() }.toMutableList()
            val piece = board[from.first][from.second]

            val jumped = kotlin.math.abs(to.first - from.first) == 2

            board[to.first][to.second] = piece.copy(
                isKing = piece.isKing || shouldBeKing(piece, to.first, p1, p2)
            )
            board[from.first][from.second] = Piece()

            if (jumped) {
                val midRow = (from.first + to.first) / 2
                val midCol = (from.second + to.second) / 2
                board[midRow][midCol] = Piece()
                soundPlayer.playCapture()
            } else {
                soundPlayer.playMove()
            }

            updateGame(game.copy(board = board))

        } while (jumped && canContinueJumping(board, to, p2, p1, p2))

        delay(300)
        updateGame(game.copy(board = board, turn = p1))
    }
}