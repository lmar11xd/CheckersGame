package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.repository.IRoomRepository
import com.lmar.checkersgame.presentation.common.components.SnackbarEvent
import com.lmar.checkersgame.presentation.common.components.SnackbarType
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.common.event.UiEvent.ShowSnackbar
import com.lmar.checkersgame.presentation.common.event.UiEvent.ToBack
import com.lmar.checkersgame.presentation.common.event.UiEvent.ToGame
import com.lmar.checkersgame.presentation.common.event.UiEvent.ToHome
import com.lmar.checkersgame.presentation.ui.event.RoomEvent
import com.lmar.checkersgame.presentation.ui.state.RoomState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: IRoomRepository
) : ViewModel() {

    companion object {
        private const val TAG = "RoomViewModel"
    }

    private val _roomState = MutableStateFlow<RoomState>(RoomState())
    val roomState: StateFlow<RoomState> = _roomState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: RoomEvent) {
        when (event) {
            is RoomEvent.CreateRoom -> {
                createRoom()
            }

            is RoomEvent.JoinRoom -> {
                searchRoomByCode()
            }

            is RoomEvent.EnteredRoomCode -> {
                _roomState.value = _roomState.value.copy(
                    roomCode = event.value
                )
            }

            is RoomEvent.ToGame -> {
                viewModelScope.launch {
                    _eventFlow.emit(ToGame(event.roomId))
                }
            }

            RoomEvent.ToBack -> {
                viewModelScope.launch {
                    _eventFlow.emit(ToBack)
                }
            }

            RoomEvent.ToHome -> {
                viewModelScope.launch {
                    _eventFlow.emit(ToHome)
                }
            }

            is RoomEvent.ShowMessage -> {
                viewModelScope.launch {
                    _eventFlow.emit(ShowSnackbar(SnackbarEvent(event.message, event.type)))
                }
            }
        }
    }

    fun createRoom() {
        viewModelScope.launch {
            val roomId = roomRepository.createRoom()
            onEvent(RoomEvent.ToGame(roomId))
        }
    }

    fun searchRoomByCode() {
        val roomCode = _roomState.value.roomCode
        if (roomCode.isEmpty()) {
            onEvent(RoomEvent.ShowMessage("¡Código de sala vacío!", SnackbarType.WARN))
            return
        }

        viewModelScope.launch {
            val room = roomRepository.getRoomByCode(roomCode)
            if (room != null) {
                _roomState.value = _roomState.value.copy(room = room)

                if (room.roomStatus == RoomStatusEnum.OPENED) {
                    roomRepository.setRoomStatus(room.roomId, RoomStatusEnum.COMPLETED)
                    onEvent(RoomEvent.ToGame(room.roomId))
                } else {
                    onEvent(RoomEvent.ShowMessage("¡Sala no encontrada, intenta con otro código!", SnackbarType.WARN))
                }
            } else {
                onEvent(RoomEvent.ShowMessage("¡Sala no encontrada, intenta con otro código!", SnackbarType.WARN))
            }
        }
    }
}