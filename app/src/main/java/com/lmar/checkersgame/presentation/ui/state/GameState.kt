package com.lmar.checkersgame.presentation.ui.state

import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.model.Game
import com.lmar.checkersgame.domain.model.Room

data class GameState (
    val game: Game = Game(),
    val room: Room = Room(),
    val gameLevel: Difficulty = Difficulty.EASY,
    val selectedCell: Position? = null,
    val scores: Map<String, Int> = emptyMap(),
    val rematchRequested: Boolean = false
)