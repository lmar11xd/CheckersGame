package com.lmar.checkersgame.presentation.common.event

import com.lmar.checkersgame.presentation.common.components.SnackbarType

sealed class ResetPasswordEvent {
    data class EnteredEmail(val value: String) : ResetPasswordEvent()
    object ResetPassword : ResetPasswordEvent()
    object ToLogin : ResetPasswordEvent()
    object ToBack : ResetPasswordEvent()

    data class ShowMessage(val message: String, val type: SnackbarType) : ResetPasswordEvent()
}