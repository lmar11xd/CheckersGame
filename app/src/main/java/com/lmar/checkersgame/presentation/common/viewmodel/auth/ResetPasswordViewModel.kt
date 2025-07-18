package com.lmar.checkersgame.presentation.common.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.util.AuthValidator
import com.lmar.checkersgame.presentation.common.components.SnackbarEvent
import com.lmar.checkersgame.presentation.common.components.SnackbarType
import com.lmar.checkersgame.presentation.common.event.ResetPasswordEvent
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.common.event.UiEvent.ShowSnackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow<String>("")
    val email: StateFlow<String> = _email

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: ResetPasswordEvent) {
        when (event) {
            is ResetPasswordEvent.EnteredEmail -> {
                _email.value = event.value
            }

            is ResetPasswordEvent.ShowMessage -> {
                viewModelScope.launch {
                    _eventFlow.emit(ShowSnackbar(SnackbarEvent(event.message, event.type)))
                }
            }

            ResetPasswordEvent.ResetPassword -> {
                resetPassword()
            }

            ResetPasswordEvent.ToLogin -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToLogin)
                }
            }

            ResetPasswordEvent.ToBack -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToBack)
                }
            }
        }
    }

    private fun resetPassword() {
        val email = _email.value
        if (email.isBlank()) {
            onEvent(
                ResetPasswordEvent.ShowMessage(
                    "¡Correo no puede ser vacío!",
                    SnackbarType.WARN
                )
            )
            return
        }
        if (!AuthValidator.isValidEmail(email)) {
            onEvent(
                ResetPasswordEvent.ShowMessage(
                    "¡Correo electrónico inválido!",
                    SnackbarType.WARN
                )
            )
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.resetPassword(email)
            result.onSuccess {
                _isLoading.value = false
                onEvent(
                    ResetPasswordEvent.ShowMessage(
                        "Se ha enviado un correo electrónico para restablecer la contraseña",
                        SnackbarType.SUCCESS
                    )
                )
                onEvent(ResetPasswordEvent.ToLogin)
            }.onFailure { error ->
                _isLoading.value = false
                onEvent(
                    ResetPasswordEvent.ShowMessage(
                        error.message ?: "Error al restablecer contraseña",
                        SnackbarType.ERROR
                    )
                )
            }
        }
    }
}