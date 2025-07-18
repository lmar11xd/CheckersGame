package com.lmar.checkersgame.presentation.common.event

import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.presentation.common.components.SnackbarEvent

sealed class UiEvent {
    data class ShowSnackbar(val snackbarEvent: SnackbarEvent) : UiEvent()
    data class ToRoute(val route: String) : UiEvent()
    object ToHome : UiEvent()
    object ToLogin : UiEvent()
    object ToSignUp : UiEvent()
    object ToProfile : UiEvent()
    object ToResetPassword : UiEvent()
    data class ToSingleGame(val level: Difficulty) : UiEvent()
    data class ToGame(val roomId: String) : UiEvent()
    object ToRanking : UiEvent()
    object ToRoom : UiEvent()
    object ToSettings : UiEvent()
    object ToBack : UiEvent()
}