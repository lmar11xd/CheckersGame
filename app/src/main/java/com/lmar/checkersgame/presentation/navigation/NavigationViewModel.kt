package com.lmar.checkersgame.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.ai.Difficulty
import com.lmar.checkersgame.presentation.common.components.SnackbarEvent
import com.lmar.checkersgame.presentation.common.components.SnackbarType
import com.lmar.checkersgame.presentation.common.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun emit(event: UiEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    fun showSnackbar(message: String, type: SnackbarType = SnackbarType.INFO) {
        emit(UiEvent.ShowSnackbar(SnackbarEvent(message, type)))
    }

    fun navigateTo(route: String) {
        emit(UiEvent.ToRoute(route))
    }

    fun navigateBack() {
        emit(UiEvent.ToBack)
    }

    fun navigateToLogin() = emit(UiEvent.ToLogin)
    fun navigateToSignUp() = emit(UiEvent.ToSignUp)
    fun navigateToHome() = emit(UiEvent.ToHome)
    fun navigateToProfile() = emit(UiEvent.ToProfile)
    fun navigateToSingleGame(level: Difficulty) = emit(UiEvent.ToSingleGame(level))
    fun navigateToGame(roomId: String) = emit(UiEvent.ToGame(roomId))
    fun navigateToRanking() = emit(UiEvent.ToRanking)
    fun navigateToRoom() = emit(UiEvent.ToRoom)
    fun navigateToSettings() = emit(UiEvent.ToSettings)
}