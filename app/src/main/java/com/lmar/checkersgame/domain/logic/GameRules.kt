package com.lmar.checkersgame.domain.logic

import com.lmar.checkersgame.domain.model.Piece
import com.lmar.checkersgame.domain.model.isEmpty
import com.lmar.checkersgame.domain.model.isNotEmpty
import kotlin.math.abs

typealias Board = List<List<Piece>>
typealias Position = Pair<Int, Int>

fun isValidMove(
    board: Board,
    from: Position,
    to: Position,
    playerId: String,
    player1Id: String,
    player2Id: String
): Boolean {
    val (fromRow, fromCol) = from
    val (toRow, toCol) = to
    val piece = board[fromRow][fromCol]

    if (piece.isEmpty()) return false
    if (piece.playerId != playerId) return false
    if (board[toRow][toCol].isNotEmpty()) return false

    val rowDiff = toRow - fromRow
    val colDiff = toCol - fromCol

    val forward = when {
        piece.isKing -> listOf(-1, 1)
        piece.playerId == player1Id -> listOf(-1)
        piece.playerId == player2Id -> listOf(1)
        else -> emptyList()
    }

    if (rowDiff in forward && abs(rowDiff) == 1 && abs(colDiff) == 1) {
        return true
    }

    if (rowDiff in forward.map { it * 2 } && abs(colDiff) == 2) {
        val midRow = (fromRow + toRow) / 2
        val midCol = (fromCol + toCol) / 2
        val middlePiece = board[midRow][midCol]
        if (middlePiece.isNotEmpty() && middlePiece.playerId != playerId) {
            return true
        }
    }

    return false
}

fun shouldBeKing(
    piece: Piece,
    toRow: Int,
    player1Id: String,
    player2Id: String
): Boolean {
    return when {
        piece.isKing -> false
        piece.playerId == player1Id && toRow == 0 -> true
        piece.playerId == player2Id && toRow == 7 -> true
        else -> false
    }
}

fun canContinueJumping(
    board: Board,
    from: Position,
    playerId: String,
    player1Id: String,
    player2Id: String
): Boolean {
    val (row, col) = from
    val piece = board[row][col]
    if (piece.isEmpty()) return false

    val directions = if (piece.isKing) {
        listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
    } else {
        when (piece.playerId) {
            player1Id -> listOf(-1 to -1, -1 to 1)
            player2Id -> listOf(1 to -1, 1 to 1)
            else -> emptyList()
        }
    }

    for ((dr, dc) in directions) {
        val midRow = row + dr
        val midCol = col + dc
        val landingRow = row + 2 * dr
        val landingCol = col + 2 * dc

        if (
            landingRow in 0..7 && landingCol in 0..7 &&
            midRow in 0..7 && midCol in 0..7
        ) {
            val midPiece = board[midRow][midCol]
            val landingPiece = board[landingRow][landingCol]

            if (
                midPiece.isNotEmpty() &&
                midPiece.playerId != playerId &&
                landingPiece.isEmpty()
            ) {
                return true
            }
        }
    }

    return false
}


fun hasAnyValidMoves(
    board: Board,
    playerId: String,
    player1Id: String,
    player2Id: String
): Boolean {
    for (row in 0..7) {
        for (col in 0..7) {
            val piece = board[row][col]
            if(piece.isEmpty()) continue
            if (piece.playerId != playerId) continue

            val directions = if (piece.isKing) {
                listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
            } else {
                when (piece.playerId) {
                    player1Id -> listOf(-1 to -1, -1 to 1)
                    player2Id -> listOf(1 to -1, 1 to 1)
                    else -> emptyList()
                }
            }

            val from = row to col
            for ((dr, dc) in directions) {
                val step = row + dr to col + dc
                val jump = row + 2 * dr to col + 2 * dc

                if (
                    step.first in 0..7 && step.second in 0..7 &&
                    isValidMove(board, from, step, playerId, player1Id, player2Id)
                ) return true

                if (
                    jump.first in 0..7 && jump.second in 0..7 &&
                    isValidMove(board, from, jump, playerId, player1Id, player2Id)
                ) return true
            }
        }
    }
    return false
}

fun hasPieces(board: Board, playerId: String): Boolean {
    return board.any { row ->
        row.any { it.playerId == playerId }
    }
}

