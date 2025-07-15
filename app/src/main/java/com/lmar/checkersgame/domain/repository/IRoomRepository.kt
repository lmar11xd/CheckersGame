package com.lmar.checkersgame.domain.repository

import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Room

interface IRoomRepository {
    fun listenForUpdates(roomId: String, onUpdate: (Room) -> Unit)
    suspend fun createRoom(): String
    suspend fun getRoomById(roomId: String): Room?
    suspend fun getRoomByCode(roomCode: String): Room?
    suspend fun updateRoom(room: Room, onResult: (Boolean) -> Unit)
    suspend fun setRoomStatus(roomId: String, status: RoomStatusEnum)
}