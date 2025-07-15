package com.lmar.checkersgame.presentation.common.event

import com.lmar.checkersgame.presentation.common.components.SnackbarEvent

sealed class UiEvent {
    data class ShowSnackbar(val snackbarEvent: SnackbarEvent) : UiEvent()
    object ToHome : UiEvent()
    object ToLogin : UiEvent()
    object ToSignUp : UiEvent()
    object ToBack : UiEvent()
}