package com.lmar.checkersgame.domain.usecase.single_game

import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.logic.canContinueJumping
import com.lmar.checkersgame.domain.logic.isValidMove
import com.lmar.checkersgame.domain.logic.shouldBeKing
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.sound.ISoundPlayer
import com.lmar.checkersgame.domain.util.requirePlayerIds
import javax.inject.Inject

class MovePieceUseCase @Inject constructor(
    private val soundPlayer: ISoundPlayer,
    private val checkWinnerUseCase: CheckWinnerUseCase,
    private val makeAIMoveUseCase: MakeAIMoveUseCase
) {
    suspend operator fun invoke(
        from: Position,
        to: Position,
        game: Game,
        userId: String,
        updateGame: (Game) -> Unit,
        updateSelected: (Position?) -> Unit,
        declareWinner: (String?) -> Unit
    ) {
        val (p1, p2) = game.requirePlayerIds()
        val piece = game.board[from.first][from.second]
        if (piece.playerId != userId ||
            !isValidMove(game.board, from, to, userId, p1, p2)) return

        val board = game.board.map { it.map { it.copy() }.toMutableList() }.toMutableList()
        val rowDiff = to.first - from.first
        val colDiff = to.second - from.second
        val jumped = kotlin.math.abs(rowDiff) == 2 && kotlin.math.abs(colDiff) == 2

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

        if (jumped && canContinueJumping(board, to, userId, p1, p2)) {
            updateSelected(to)
        } else {
            updateSelected(null)
            val winner = checkWinnerUseCase(board, game)
            declareWinner(winner)
            if (winner == null) {
                makeAIMoveUseCase(game.copy(board = board), updateGame)
            }
        }
    }
}