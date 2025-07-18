package com.lmar.checkersgame.presentation.common.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.model.User
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.domain.util.AuthValidator
import com.lmar.checkersgame.presentation.common.components.SnackbarEvent
import com.lmar.checkersgame.presentation.common.components.SnackbarType
import com.lmar.checkersgame.presentation.common.event.AuthEvent
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.common.event.UiEvent.ShowSnackbar
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
    private val authRepository: IAuthRepository,
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
                    _eventFlow.emit(ShowSnackbar(SnackbarEvent(event.message, event.type)))
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

            AuthEvent.ToResetPassword -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToResetPassword)
                }
            }
        }
    }

    fun checkAuthStatus() {
        val userId = authRepository.getCurrentUser()?.uid
        if (userId != null) {
            viewModelScope.launch {
                val user = userRepository.getUserById(userId) ?: User()
                _authState.value = _authState.value.copy(
                    isAuthenticated = true,
                    user = user
                )
            }
        } else {
            _authState.value = _authState.value.copy(
                isAuthenticated = false,
                user = User()
            )
        }
    }

    fun login() {
        val email = _authState.value.email
        val password = _authState.value.password

        if (email.isBlank() || password.isBlank()) {
            onEvent(
                AuthEvent.ShowMessage(
                    "¡Correo y/o contraseña no pueden ser vacías!",
                    SnackbarType.WARN
                )
            )
            return
        }

        val validationResult = AuthValidator.validateCredentials(email, password)
        validationResult.onFailure {
            onEvent(
                AuthEvent.ShowMessage(
                    it.message ?: "Error al validar datos del usuario",
                    SnackbarType.WARN
                )
            )
            return
        }

        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = true
                )
                onEvent(
                    AuthEvent.ShowMessage(
                        "Usuario logueado correctamente",
                        SnackbarType.SUCCESS
                    )
                )
                onEvent(AuthEvent.ToHome)
            }.onFailure { error ->
                _authState.value = _authState.value.copy(isLoading = false)
                onEvent(
                    AuthEvent.ShowMessage(
                        error.message ?: "Error al logearse",
                        SnackbarType.ERROR
                    )
                )
            }
        }
    }

    fun signup() {
        val names = _authState.value.names
        val email = _authState.value.email
        val password = _authState.value.password
        val confirmPassword = _authState.value.confirmPassword

        if (names.isBlank()) {
            onEvent(AuthEvent.ShowMessage("¡Nombres no pueden ser vacíos!", SnackbarType.WARN))
            return
        }

        if (email.isBlank()) {
            onEvent(AuthEvent.ShowMessage("¡Correo no puede ser vacío!", SnackbarType.WARN))
            return
        }

        if (!AuthValidator.isValidEmail(email)) {
            onEvent(AuthEvent.ShowMessage("¡Correo electrónico inválido!", SnackbarType.WARN))
            return
        }

        if (password.isBlank()) {
            onEvent(AuthEvent.ShowMessage("¡Contraseña no puede ser vacía!", SnackbarType.WARN))
            return
        }

        if (password.length < 6) {
            onEvent(
                AuthEvent.ShowMessage(
                    "¡Contraseña debe tener al menos 6 caracteres!",
                    SnackbarType.WARN
                )
            )
            return
        }

        if (confirmPassword.isBlank() || password != confirmPassword
        ) {
            onEvent(AuthEvent.ShowMessage("¡Las contraseñas no coinciden!", SnackbarType.WARN))
            return
        }

        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            val result = authRepository.signup(_authState.value.email, _authState.value.password)
            result.onSuccess {
                val uid = result.getOrNull()

                // Navegar o registrar en la base de datos
                val newUser = User().apply {
                    id = uid.toString()
                    this.names = names
                    this.email = email
                }

                val success = userRepository.createUser(newUser)
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = success
                )
                if (success) {
                    onEvent(
                        AuthEvent.ShowMessage(
                            "Usuario registrado correctamente",
                            SnackbarType.SUCCESS
                        )
                    )
                    onEvent(AuthEvent.ToHome)
                } else {
                    onEvent(
                        AuthEvent.ShowMessage(
                            "Error al registrar datos del usuario",
                            SnackbarType.ERROR
                        )
                    )
                }
            }.onFailure { error ->
                _authState.value = _authState.value.copy(isLoading = false)
                onEvent(
                    AuthEvent.ShowMessage(
                        error.message ?: "Error al registrar usuario",
                        SnackbarType.ERROR
                    )
                )
            }
        }
    }
}