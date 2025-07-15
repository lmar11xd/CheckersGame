package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.domain.repository.IRoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: IRoomRepository
) : ViewModel() {

    companion object {
        private const val TAG = "RoomViewModel"
    }

    private val _roomState = MutableLiveData<Room?>()
    val roomState: LiveData<Room?> = _roomState

    fun createRoom(onResult: (roomId: String) -> Unit) {
        viewModelScope.launch {
            val roomId = roomRepository.createRoom()
            onResult(roomId)
        }
    }

    fun searchRoomByCode(roomCode: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val room = roomRepository.getRoomByCode(roomCode)
            if (room != null) {
                _roomState.value = room

                if (room.roomStatus == RoomStatusEnum.OPENED) {
                    roomRepository.setRoomStatus(room.roomId, RoomStatusEnum.COMPLETED)
                    onResult(true, room.roomId)
                } else {
                    onResult(false, "¡Sala llena!")
                }
            } else {
                onResult(false, "¡Sala no encontrada, intenta con otro código!")
            }
        }
    }
}