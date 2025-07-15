package com.lmar.checkersgame.presentation.common.event

import com.lmar.checkersgame.presentation.common.components.SnackbarType

sealed class AuthEvent {
    data class EnteredEmail(val value: String) : AuthEvent()
    data class EnteredPassword(val value: String) : AuthEvent()
    data class EnteredConfirmPassword(val value: String) : AuthEvent()
    data class EnteredNames(val value: String) : AuthEvent()
    data class EnteredImageUrl(val value: String) : AuthEvent()

    // Snackbar Events
    data class ShowMessage(
        val message: String, val type: SnackbarType = SnackbarType.INFO
    ) : AuthEvent()

    // Authentication Events
    object Login : AuthEvent()
    object SignUp : AuthEvent()

    // Navigation Events
    object ToHome : AuthEvent()
    object ToLogin : AuthEvent()
    object ToSignUp : AuthEvent()
    object ToBack : AuthEvent()
}