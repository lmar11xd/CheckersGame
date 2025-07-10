package com.lmar.checkersgame.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.repository.IRoomRepository
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.core.utils.generateUniqueCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
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

    fun createRoom(onResult: (roomId: String?) -> Unit) {
        val roomId = UUID.randomUUID().toString()
        val currentTimestamp = System.currentTimeMillis()

        val newRoom = Room()
        newRoom.roomId = roomId
        newRoom.roomCode = generateUniqueCode()
        newRoom.createdAt = currentTimestamp
        newRoom.updatedAt = currentTimestamp

        Log.d(TAG, "Creando Sala: $roomId")

        viewModelScope.launch {
            roomRepository.createRoom(newRoom) { success ->
                if (success) {
                    onResult(roomId)
                }
            }
        }
    }

    fun searchRoomByCode(roomCode: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            roomRepository.getRoomByCode(roomCode) { room ->
                if (room != null) {
                    _roomState.value = room

                    if (room.roomStatus == RoomStatusEnum.OPENED) {
                        val updatedRoom = room.copy(
                            roomStatus = RoomStatusEnum.COMPLETED,
                            updatedAt = System.currentTimeMillis()
                        )

                        viewModelScope.launch {
                            roomRepository.updateRoom(updatedRoom) { success ->
                                if (success) {
                                    onResult(true, room.roomId)
                                } else {
                                    onResult(false, "Error al actualizar la sala")
                                }
                            }
                        }
                    } else {
                        onResult(false, "¡Sala llena!")
                    }
                } else {
                    onResult(false, "¡Sala no encontrada, intenta con otro código!")
                }
            }
        }
    }
}