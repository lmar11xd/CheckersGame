package com.lmar.checkersgame.presentation.common.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.core.utils.Constants
import com.lmar.checkersgame.domain.model.User
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.presentation.common.components.SnackbarEvent
import com.lmar.checkersgame.presentation.common.components.SnackbarType
import com.lmar.checkersgame.presentation.common.event.AuthEvent
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.common.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: IAuthRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        checkAuthStatus()
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EnteredEmail -> {
                _authState.value = _authState.value.copy(
                    email = event.value
                )
            }

            is AuthEvent.EnteredPassword -> {
                _authState.value = _authState.value.copy(
                    password = event.value
                )
            }

            is AuthEvent.EnteredConfirmPassword -> {
                _authState.value = _authState.value.copy(
                    confirmPassword = event.value
                )
            }

            is AuthEvent.EnteredNames -> {
                _authState.value = _authState.value.copy(
                    names = event.value
                )
            }

            is AuthEvent.EnteredImageUrl -> {
                _authState.value = _authState.value.copy(
                    imageUrl = event.value
                )
            }

            AuthEvent.Login -> {
                login()
            }

            AuthEvent.SignUp -> {
                signup()
            }

            is AuthEvent.ShowMessage -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowSnackbar(SnackbarEvent(event.message, event.type)))
                }
            }

            AuthEvent.ToHome -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToHome)
                }
            }

            AuthEvent.ToLogin -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToLogin)
                }
            }

            AuthEvent.ToSignUp -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToSignUp)
                }
            }

            AuthEvent.ToBack -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToBack)
                }
            }
        }
    }

    fun checkAuthStatus() {
        val isAuthenticated = repository.isAuthenticated()
        _authState.value = _authState.value.copy(
            isAuthenticated = isAuthenticated
        )
    }

    fun login() {
        if (_authState.value.email.isBlank() || _authState.value.password.isBlank()) {
            onEvent(
                AuthEvent.ShowMessage(
                    "¡Correo y/o contraseña no pueden ser vacías!",
                    SnackbarType.WARN
                )
            )
            return
        }

        _authState.value = _authState.value.copy(isLoading = true)
        repository.login(_authState.value.email, _authState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true
                    )
                    onEvent(AuthEvent.ToHome)
                }
            }
            .addOnFailureListener { error ->
                _authState.value = _authState.value.copy(isLoading = false)
                Log.e(TAG, "Error al logearse: ${error.message}")
                if (error.message == Constants.ERROR_MESSAGE_AUTH) {
                    onEvent(
                        AuthEvent.ShowMessage(
                            "Correo y/o contraseña incorrectos",
                            SnackbarType.WARN
                        )
                    )
                } else {
                    onEvent(AuthEvent.ShowMessage("Error al logearse", SnackbarType.ERROR))
                }
            }
    }

    fun signup() {
        if (_authState.value.names.isBlank()) {
            onEvent(AuthEvent.ShowMessage("¡Nombres no pueden ser vacíos!", SnackbarType.WARN))
            return
        }

        if (_authState.value.email.isBlank()) {
            onEvent(AuthEvent.ShowMessage("¡Correo no puede ser vacío!", SnackbarType.WARN))
            return
        }

        if (_authState.value.password.isBlank()) {
            onEvent(AuthEvent.ShowMessage("¡Contraseña no puede ser vacía!", SnackbarType.WARN))
            return
        }

        if (_authState.value.password.length < 6) {
            onEvent(
                AuthEvent.ShowMessage(
                    "¡Contraseña debe tener al menos 6 caracteres!",
                    SnackbarType.WARN
                )
            )
            return
        }

        if (_authState.value.confirmPassword.isBlank() ||
            _authState.value.password != _authState.value.confirmPassword
        ) {
            onEvent(AuthEvent.ShowMessage("¡Las contraseñas no coinciden!", SnackbarType.WARN))
            return
        }

        _authState.value = _authState.value.copy(isLoading = true)

        repository.signup(_authState.value.email, _authState.value.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Registrar Usuario
                    val newUser = User().apply {
                        id = task.result.user?.uid ?: ""
                        this.names = names
                        this.email = email
                    }

                    viewModelScope.launch {
                        userRepository.createUser(newUser) { success ->
                            _authState.value = _authState.value.copy(
                                isLoading = false,
                                isAuthenticated = success
                            )

                            if (success) {
                                Log.d(TAG, "Usuario registrado con éxito: ${newUser.id}")
                                onEvent(AuthEvent.ToHome)
                            } else {
                                Log.e(TAG, "Error al registrar usuario")
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { error ->
                _authState.value = _authState.value.copy(isLoading = false)
                Log.e(TAG, "Error al registrar usuario: ${error.message}")
                if (error.message == Constants.ERROR_MESSAGE_ACCOUNT_EXISTS) {
                    onEvent(
                        AuthEvent.ShowMessage(
                            "El correo ya ha sido registrado",
                            SnackbarType.WARN
                        )
                    )
                } else {
                    onEvent(AuthEvent.ShowMessage("Error al registrar usuario", SnackbarType.ERROR))
                }
            }
    }

    fun signout() {
        repository.signout()
        _authState.value = _authState.value.copy(
            isAuthenticated = false
        )
    }
}