package com.lmar.checkersgame.presentation.ui.state

import com.lmar.checkersgame.domain.model.Room

data class RoomState (
    val room: Room = Room(),
    val roomCode: String = ""
)