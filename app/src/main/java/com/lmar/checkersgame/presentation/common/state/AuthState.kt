package com.lmar.checkersgame.presentation.common.state

import com.lmar.checkersgame.domain.model.User

data class AuthState(
    val user: User = User(),
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val names: String = "",
    val imageUrl: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
)