package com.lmar.checkersgame.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.lmar.checkersgame.core.utils.generateUniqueCode
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.domain.repository.IRoomRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseRoomRepository @Inject constructor(
    private val database: DatabaseReference
) : IRoomRepository {

    companion object {
        private const val TAG = "FirebaseRoomRepository"
    }

    override fun listenForUpdates(roomId: String): Flow<Room> = callbackFlow {
        if (roomId.isBlank()) {
            close(IllegalArgumentException("roomId no puede estar vacío"))
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Room::class.java)?.let { trySend(it).isSuccess }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        val ref = database.child(roomId)
        ref.addValueEventListener(listener)

        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun createRoom(): String {
        val roomId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val newRoom = Room(
            roomId = roomId,
            roomCode = generateUniqueCode(),
            createdAt = timestamp,
            updatedAt = timestamp
        )

        try {
            database.child(roomId).setValue(newRoom).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear sala con ID=$roomId", e)
            throw e
        }

        return roomId
    }

    override suspend fun getRoomById(roomId: String): Room? {
        if (roomId.isBlank()) return null

        return try {
            val snapshot = database.child(roomId).get().await()
            snapshot.getValue(Room::class.java)
        } catch (ex: Exception) {
            Log.e(TAG, "Error al obtener sala con ID=$roomId", ex)
            null
        }
    }

    override suspend fun getRoomByCode(roomCode: String): Room? {
        if (roomCode.isBlank()) return null

        return try {
            val query = database
                .orderByChild("roomCode")
                .equalTo(roomCode)
                .limitToFirst(1)

            val snapshot = query.get().await()
            snapshot.children.firstOrNull()?.getValue(Room::class.java)
        } catch (ex: Exception) {
            Log.e(TAG, "Error al obtener sala con código=$roomCode", ex)
            null
        }
    }

    override suspend fun updateRoom(room: Room): Boolean {
        val updatedRoom = room.copy(updatedAt = System.currentTimeMillis())
        return try {
            database.child(updatedRoom.roomId).setValue(updatedRoom).await()
            true
        } catch (ex: Exception) {
            Log.e(TAG, "Error al actualizar sala con ID=${room.roomId}", ex)
            false
        }
    }

    override suspend fun setRoomStatus(roomId: String, status: RoomStatusEnum) {
        if (roomId.isBlank()) return

        try {
            database.child(roomId).child("status").setValue(status).await()
        } catch (ex: Exception) {
            Log.e(TAG, "Error al establecer estado $status para sala ID=$roomId", ex)
        }
    }
}