package com.lmar.checkersgame.domain.repository.common

import android.net.Uri
import com.lmar.checkersgame.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun listenForUpdates(userId: String): Flow<User>
    suspend fun createUser(user: User): Boolean
    suspend fun getUserById(userId: String): User?
    suspend fun uploadProfileImage(userId: String, uri: Uri): String?
    suspend fun updateUser(user: User): Boolean
    suspend fun updateUserScore(userId: String, newScore: Int): Boolean
    suspend fun getTopPlayers(limit: Int = 10): List<User>
}