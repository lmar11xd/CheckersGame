package com.lmar.checkersgame.domain.model

import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.domain.enum.GameStatusEnum

data class Game(
    var roomId: String = "",
    val player1: Player? = null,
    val player2: Player? = null,
    var board: List<List<Piece>> = emptyList(),
    var turn: String = "",
    var winner: String? = null,
    var status: GameStatusEnum = GameStatusEnum.WAITING,
    val rematchRequests: Map<String, Boolean> = emptyMap(),
    val level: Difficulty = Difficulty.EASY,
    var createdAt: Long? = null,
    var updatedAt: Long? = null
)