package com.lmar.checkersgame.presentation.ui.state

import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.domain.logic.Position
import com.lmar.checkersgame.domain.model.Game

data class GameState (
    val game: Game = Game(),
    val gameLevel: Difficulty = Difficulty.EASY,
    val selectedCell: Position? = null,
    val scores: Map<String, Int> = emptyMap(),
    val rematchRequested: Boolean = false
)