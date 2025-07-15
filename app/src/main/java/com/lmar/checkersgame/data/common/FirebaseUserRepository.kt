package com.lmar.checkersgame.data.common

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.lmar.checkersgame.domain.model.User
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.core.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor() : IUserRepository {

    companion object {
        private const val TAG = "FirebaseUserRepository"
    }

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference(Constants.USERS_REFERENCE)
    private val storage = FirebaseStorage.getInstance()

    override suspend fun createUser(user: User, onResult: (Boolean) -> Unit) {
        database.child(user.id).setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Usuario registrado con éxito: ${user.id}")
                onResult(true)
            }
            .addOnFailureListener {
                Log.e(TAG, "Error al actualizar usuario", it)
                onResult(false)
            }
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = database.child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener usuario: ${e.message}", e)
            null
        }
    }

    override fun listenForUpdates(userId: String, onUpdate: (User) -> Unit) {
        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(User::class.java)?.let { onUpdate(it) }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override suspend fun uploadProfileImage(
        userId: String,
        uri: Uri,
        onResult: (Boolean, String?) -> Unit
    ) {
        val ref = storage.getReference("${Constants.STORAGE_REFERENCE}/$userId.jpg")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onResult(true, downloadUri.toString())
                }.addOnFailureListener {
                    onResult(false, null)
                }
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Error al guardar imagen de perfil", error)
                onResult(false, null)
            }
    }

    override suspend fun updateUser(user: User ) {
        try {
            database.child(user.id).setValue(user)
            Log.d(TAG, "Usuario actualizado con éxito: ${user.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar usuario", e)
        }
    }

    override suspend fun updateUserScore(userId: String, newScore: Int) {
        database.child(userId).child("score").setValue(newScore)
    }

    override suspend fun getTopPlayers(limit: Int): List<User> {
        val query = database.orderByChild("score")
            .limitToLast(limit) // ← porque ordena ascendente, usamos limitToLast
        val snapshot = query.get().await()

        if (snapshot == null) return emptyList()

        val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
            .sortedByDescending { it.score }

        return users
    }
}