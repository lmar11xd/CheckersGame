package com.lmar.checkersgame.domain.repository.common

import android.net.Uri
import com.lmar.checkersgame.domain.model.User

interface IUserRepository {
    fun listenForUpdates(userId: String, onUpdate: (User) -> Unit)
    suspend fun createUser(user: User, onResult: (Boolean) -> Unit)
    suspend fun getUserById(userId: String, onResult: (User?) -> Unit)
    suspend fun getUserById(userId: String): User?
    suspend fun uploadProfileImage(userId: String, uri: Uri, onResult: (Boolean, String?) -> Unit)
    suspend fun updateUser(user: User, onResult: (Boolean) -> Unit)
    suspend fun updateUserScore(userId: String, newScore: Int)
    suspend fun getTopPlayers(limit: Int = 10): List<User>
}