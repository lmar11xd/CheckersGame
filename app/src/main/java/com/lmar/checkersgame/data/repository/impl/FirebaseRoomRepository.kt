package com.lmar.checkersgame.data.repository.impl

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lmar.checkersgame.data.repository.IRoomRepository
import com.lmar.checkersgame.domain.enum.RoomStatusEnum
import com.lmar.checkersgame.domain.model.Room
import com.lmar.checkersgame.utils.Constants
import javax.inject.Inject

class FirebaseRoomRepository @Inject constructor(): IRoomRepository {

    companion object {
        private const val TAG = "FirebaseRoomRepository"
    }

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("${Constants.DATABASE_REFERENCE}/${Constants.ROOMS_REFERENCE}")

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

    override suspend fun createRoom(
        room: Room,
        onResult: (Boolean) -> Unit
    ) {
        database.child(room.roomId)
            .setValue(room)
            .addOnSuccessListener {
                Log.d(TAG, "Sala creada con éxito: ${room.roomId}")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error al crear sala", e)
                onResult(false)
            }
    }

    override suspend fun getRoomById(
        roomId: String,
        onResult: (Room?) -> Unit
    ) {
        database.child(roomId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val room = snapshot.getValue(Room::class.java)
                    onResult(room)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error al obtener sala: ${error.message}")
                    onResult(null)
                }
            })
    }

    override suspend fun getRoomByCode(
        roomCode: String,
        onResult: (Room?) -> Unit
    ) {
        database
            .orderByChild("roomCode")
            .equalTo(roomCode)
            .limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (roomSnapshot in snapshot.children) {
                        val room = roomSnapshot.getValue(Room::class.java)
                        Log.d(TAG, "Sala con codigo: $roomCode encontrada")
                        onResult(room)
                        return
                    }

                    Log.d(TAG, "Sala con codigo: $roomCode no encontrada")
                    onResult(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error de consulta: $error")
                    onResult(null)
                }

            })
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