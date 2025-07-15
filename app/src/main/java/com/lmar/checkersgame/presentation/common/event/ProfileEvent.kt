package com.lmar.checkersgame.presentation.common.event

import android.net.Uri
import com.lmar.checkersgame.presentation.common.components.SnackbarType

sealed class ProfileEvent {
    data class EnteredNames(val value: String): ProfileEvent()
    data class EnteredImageUri(val value: Uri): ProfileEvent()
    data class ShowForm(val value: Boolean): ProfileEvent()

    // Snackbars Events
    data class ShowMessage(val message: String, val type: SnackbarType) : ProfileEvent()

    // Form Events
    object SaveForm: ProfileEvent()
    object SignOut: ProfileEvent()

    // Navigation Events
    object ToLogin : ProfileEvent()
    object ToSignUp : ProfileEvent()
    object ToBack: ProfileEvent()
}