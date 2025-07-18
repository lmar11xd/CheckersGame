package com.lmar.checkersgame.domain.repository

import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Room
import kotlinx.coroutines.flow.Flow

interface IRoomRepository {
    fun listenForUpdates(roomId: String): Flow<Room>
    suspend fun createRoom(): String
    suspend fun getRoomById(roomId: String): Room?
    suspend fun getRoomByCode(roomCode: String): Room?
    suspend fun updateRoom(room: Room): Boolean
    suspend fun setRoomStatus(roomId: String, status: RoomStatusEnum)
}