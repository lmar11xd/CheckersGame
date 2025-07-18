package com.lmar.checkersgame.domain.repository.common

import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun signup(email: String, password: String): Result<String>
    suspend fun resetPassword(email: String): Result<Unit>
    fun signOut()
    fun isAuthenticated(): Boolean
    fun getCurrentUser(): FirebaseUser?
}