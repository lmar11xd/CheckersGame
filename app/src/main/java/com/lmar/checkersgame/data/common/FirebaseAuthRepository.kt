package com.lmar.checkersgame.data.common

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : IAuthRepository {

    companion object {
        private const val TAG = "FirebaseAuthRepository"
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            when(e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Log.e(TAG, "Correo y/o contraseña incorrectos", e)
                    Result.failure(Exception("Correo y/o contraseña incorrectos"))
                }
                else -> {
                    Log.e(TAG, "Error al iniciar sesión", e)
                    Result.failure(Exception("Error al iniciar sesión"))
                }
            }
        }
    }

    override suspend fun signup(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("UID no disponible"))
            Result.success(userId)
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthUserCollisionException -> {
                    Log.e(TAG, "El correo ya está registrado", e)
                    Result.failure(Exception("El correo ya está registrado"))
                }
                else -> {
                    Log.e(TAG, "Error al registrar usuario", e)
                    Result.failure(Exception("Error al registrar usuario"))
                }
            }
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al restablecer contraseña", e)
            Result.failure(e)
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun isAuthenticated(): Boolean =
        auth.currentUser != null

    override fun getCurrentUser(): FirebaseUser? =
        auth.currentUser
}