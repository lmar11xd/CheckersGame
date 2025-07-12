package com.lmar.checkersgame.domain.util

import com.lmar.checkersgame.domain.model.Game

fun Game.requirePlayerIds(): Pair<String, String> {
    val player1Id = this.player1?.id ?: error("Player1 not defined")
    val player2Id = this.player2?.id ?: error("Player2 not defined")
    return player1Id to player2Id
}