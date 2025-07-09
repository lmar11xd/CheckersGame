package com.lmar.checkersgame.domain.logic

import com.lmar.checkersgame.domain.model.Piece

fun generateInitialBoard(player1Id: String = "player1", player2Id: String = "player2"): List<List<Piece>> {
    return List(8) { row ->
        List(8) { col ->
            when {
                row < 3 && (row + col) % 2 == 1 -> Piece(playerId = player2Id)
                row > 4 && (row + col) % 2 == 1 -> Piece(playerId = player1Id)
                else -> Piece() // Pieza vacía
            }
        }
    }
}