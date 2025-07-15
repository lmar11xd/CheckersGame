package com.lmar.checkersgame.presentation.ui.event

import com.lmar.checkersgame.presentation.common.components.SnackbarType

sealed class RoomEvent {
    data class EnteredRoomCode(val value: String): RoomEvent()
    data class ShowMessage(val message: String, val type: SnackbarType): RoomEvent()

    object CreateRoom: RoomEvent()
    object JoinRoom: RoomEvent()

    data class ToGame(val roomId: String): RoomEvent()
    object ToHome: RoomEvent()
    object ToBack: RoomEvent()
}