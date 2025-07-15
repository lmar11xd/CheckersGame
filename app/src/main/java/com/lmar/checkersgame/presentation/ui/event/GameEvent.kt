package com.lmar.checkersgame.presentation.ui.event

sealed class GameEvent {
    data class CellClicked(val row: Int, val col: Int) : GameEvent()
    object Rematch : GameEvent()
    object ToBack : GameEvent()
}