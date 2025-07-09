package com.lmar.checkersgame.domain.model

data class Piece(
    var playerId: String = "",
    var isKing: Boolean = false
) {
    constructor() : this("", false)
}

fun Piece.isEmpty(): Boolean = this.playerId.isBlank()
fun Piece.isNotEmpty(): Boolean = this.playerId.isNotBlank()