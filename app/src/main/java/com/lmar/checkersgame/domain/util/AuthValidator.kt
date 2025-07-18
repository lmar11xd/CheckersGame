package com.lmar.checkersgame.domain.util

import android.util.Patterns

object AuthValidator {

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
        // Puedes mejorar:
        // return password.matches(Regex("^(?=.*[A-Z])(?=.*\\d).{6,}\$"))
    }

    fun validateCredentials(email: String, password: String): Result<Unit> {
        return when {
            !isValidEmail(email) -> Result.failure(IllegalArgumentException("Correo electrónico inválido"))
            !isValidPassword(password) -> Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
            else -> Result.success(Unit)
        }
    }
}