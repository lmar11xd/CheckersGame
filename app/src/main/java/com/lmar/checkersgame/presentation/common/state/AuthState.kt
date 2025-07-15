package com.lmar.checkersgame.presentation.common.state

data class AuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val names: String = "",
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
)