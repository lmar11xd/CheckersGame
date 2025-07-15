package com.lmar.checkersgame.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lmar.checkersgame.domain.repository.IRoomRepository
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.core.utils.Constants
import com.lmar.checkersgame.core.utils.generateUniqueCode
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseRoomRepository @Inject constructor() : IRoomRepository {

    companion object {
        private const val TAG = "FirebaseRoomRepository"
    }

    private val database: DatabaseReference = FirebaseDatabase.getInstance()
        .getReference("${Constants.DATABASE_REFERENCE}/${Constants.ROOMS_REFERENCE}")

    override fun listenForUpdates(
        roomId: String,
        onUpdate: (Room) -> Unit
    ) {
        database.child(roomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Room::class.java)?.let { onUpdate(it) }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override suspend fun createRoom(): String {
        val roomId = UUID.randomUUID().toString()
        val currentTimestamp = System.currentTimeMillis()

        val newRoom = Room()
        newRoom.roomId = roomId
        newRoom.roomCode = generateUniqueCode()
        newRoom.createdAt = currentTimestamp
        newRoom.updatedAt = currentTimestamp

        database.child(roomId).setValue(newRoom).await()

        return roomId
    }

    override suspend fun getRoomById(roomId: String): Room? {
        return try {
            val snapshot = database.child(roomId).get().await()
            snapshot.getValue(Room::class.java)
        } catch (ex: Exception) {
            Log.e(TAG, "Error al obtener sala: ${ex.message}", ex)
            null
        }
    }

    override suspend fun getRoomByCode(roomCode: String): Room? {
        val query = database
            .orderByChild("roomCode")
            .equalTo(roomCode)
            .limitToFirst(1)

        val snapshot = query.get().await()

        if (snapshot != null) {
            for (roomSnapshot in snapshot.children) {
                return roomSnapshot.getValue(Room::class.java)
            }
        }
        return null
    }

    override suspend fun updateRoom(
        room: Room,
        onResult: (Boolean) -> Unit
    ) {
        room.updatedAt = System.currentTimeMillis()
        database.child(room.roomId).setValue(room)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { error ->
                Log.e(TAG, "Error al actualizar sala", error)
                onResult(false)
            }
    }

    override suspend fun setRoomStatus(
        roomId: String,
        status: RoomStatusEnum
    ) {
        database.child(roomId).child("status").setValue(status)
    }
}