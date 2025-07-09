package com.lmar.checkersgame.data.common

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
    fun login(email: String, password: String): Task<AuthResult>
    fun signup(email: String, password: String): Task<AuthResult>
    fun signout()
    fun isAuthenticated(): Boolean
    fun getCurrentUser(): FirebaseUser?
}