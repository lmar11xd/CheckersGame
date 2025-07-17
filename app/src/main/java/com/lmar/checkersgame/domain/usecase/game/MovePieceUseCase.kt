package com.lmar.checkersgame.domain.usecase.game

import com.lmar.checkersgame.core.utils.Constants
import com.lmar.checkersgame.data.sound.SoundPlayerWrapper
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.logic.canContinueJumping
import com.lmar.checkersgame.domain.logic.hasPieces
import com.lmar.checkersgame.domain.logic.isValidMove
import com.lmar.checkersgame.domain.logic.shouldBeKing
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.util.requirePlayerIds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

class MovePieceUseCase(
    private val repository: IGameRepository,
    private val scope: CoroutineScope,
    private val userId: String,
    private val gameId: String,
    private val soundPlayer: SoundPlayerWrapper
) {
    fun execute(
        game: Game,
        from: Position,
        to: Position,
        onUpdate: (Game) -> Unit,
        updateSelected: (Position?) -> Unit,
        onGameEnd: (String) -> Unit
    ) {
        val (player1Id, player2Id) = game.requirePlayerIds()
        val piece = game.board[from.first][from.second]

        if (piece.playerId != userId || !isValidMove(game.board, from, to, userId, player1Id, player2Id)) return

        val newBoard = game.board.map { row -> row.map { it.copy() }.toMutableList() }.toMutableList()

        val isCrowning = shouldBeKing(piece, to.first, player1Id, player2Id)
        val rowDiff = to.first - from.first
        val colDiff = to.second - from.second
        val jumped = abs(rowDiff) == 2 && abs(colDiff) == 2

        val updatedScores = game.scores.toMutableMap()
        val currentScore = updatedScores[userId] ?: 0

        when {
            isCrowning && !piece.isKing -> {
                soundPlayer.playCrown()
                updatedScores[userId] = currentScore + Constants.POINTS_CROWNING
            }
            jumped -> {
                soundPlayer.playCapture()
                updatedScores[userId] = currentScore + Constants.POINTS_CAPTURE_PIECE
            }
            else -> soundPlayer.playMove()
        }

        newBoard[to.first][to.second] = piece.copy(isKing = piece.isKing || isCrowning)
        newBoard[from.first][from.second] = Piece()

        if (jumped) {
            val midRow = (from.first + to.first) / 2
            val midCol = (from.second + to.second) / 2
            newBoard[midRow][midCol] = Piece()
        }

        val updatedGame = game.copy(board = newBoard, scores = updatedScores)
        onUpdate(updatedGame)

        if (jumped && canContinueJumping(newBoard, to, userId, player1Id, player2Id)) {
            updateSelected(to)
            return
        }

        updateSelected(null)

        val opponentId = if (userId == player1Id) player2Id else player1Id

        if (!hasPieces(newBoard, opponentId)) {
            onGameEnd(userId)
        } else {
            scope.launch {
                repository.updateBoard(gameId, newBoard)
                repository.updateTurn(gameId, opponentId)
            }
        }
    }
}