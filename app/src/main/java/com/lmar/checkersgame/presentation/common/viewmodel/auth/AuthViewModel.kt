package com.lmar.checkersgame.presentation.common.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.data.common.IAuthRepository
import com.lmar.checkersgame.data.common.IUserRepository
import com.lmar.checkersgame.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        _authState.value = if (repository.isAuthenticated()) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("¡Correo y/o contraseña no pueden ser vacías!")
            return
        }

        _authState.value = AuthState.Loading
        repository.login(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Error al logearse")
                }
            }
            .addOnFailureListener { error ->
                _authState.value = error.message?.let { AuthState.Error(it) }
            }
    }

    fun signup(names: String, email: String, password: String, passwordRepeat: String) {
        if(names.isEmpty()) {
            _authState.value = AuthState.Error("¡Nombres no puede ser vacío!")
            return
        }

        if(email.isEmpty()) {
            _authState.value = AuthState.Error("¡Correo no puede ser vacío!")
            return
        }

        if(password.isEmpty()) {
            _authState.value = AuthState.Error("¡Contraseña no puede ser vacía!")
            return
        }

        if(password.length < 6) {
            _authState.value = AuthState.Error("¡Contraseña debe tener al menos 6 caracteres!")
            return
        }

        if(passwordRepeat.isEmpty() || password != passwordRepeat) {
            _authState.value = AuthState.Error("¡Las contraseñas no coinciden!")
            return
        }

        _authState.value = AuthState.Loading
        repository.signup(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Authenticated

                    //Registrar Usuario
                    val newUser = User().apply {
                        id = task.result.user?.uid ?: ""
                        this.names = names
                        this.email = email
                    }

                    viewModelScope.launch {
                        userRepository.createUser(newUser) { success ->
                            if (success) {
                                Log.d(TAG, "Usuario registrado con éxito: ${newUser.id}")
                            } else {
                                Log.e(TAG, "Error al registrar usuario")
                            }
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Error al registrarse")
                }
            }
            .addOnFailureListener { error ->
                _authState.value = error.message?.let { AuthState.Error(it) }
            }
    }

    fun signout() {
        repository.signout()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    data object Authenticated: AuthState()
    data object Unauthenticated: AuthState()
    data object Loading: AuthState()
    data class Error(val message: String): AuthState()
}