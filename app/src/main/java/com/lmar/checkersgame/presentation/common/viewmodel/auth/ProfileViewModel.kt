package com.lmar.checkersgame.presentation.common.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.model.User
import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.presentation.common.components.SnackbarEvent
import com.lmar.checkersgame.presentation.common.components.SnackbarType
import com.lmar.checkersgame.presentation.common.event.ProfileEvent
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.common.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        val userId = authRepository.getCurrentUser()?.uid
        _profileState.value = _profileState.value.copy(isAuthenticated = userId != null)
        if (userId != null) {
            getUserById(userId)
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.EnteredNames -> {
                _profileState.value = _profileState.value.copy(
                    user = _profileState.value.user.copy(names = event.value)
                )
            }

            is ProfileEvent.EnteredImageUri -> {
                _profileState.value = _profileState.value.copy(imageUri = event.value)
            }


            is ProfileEvent.ShowForm -> {
                _profileState.value = _profileState.value.copy(isShowingForm = event.value)
            }

            ProfileEvent.SaveForm -> {
                saveForm()
            }

            ProfileEvent.SignOut -> {
                logout()
            }

            is ProfileEvent.ShowMessage -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowSnackbar(SnackbarEvent(event.message, event.type)))
                }
            }

            ProfileEvent.ToBack -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToBack)
                }
            }

            ProfileEvent.ToLogin -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToLogin)
                }
            }

            ProfileEvent.ToSignUp -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToSignUp)
                }
            }
        }
    }

    private fun logout() {
        authRepository.signOut()
        _profileState.value = _profileState.value.copy(isAuthenticated = false)
    }

    private fun getUserById(userId: String) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)
            val user = userRepository.getUserById(userId)
            _profileState.value = _profileState.value.copy(isLoading = false)
            if (user != null) {
                _profileState.value = _profileState.value.copy(user = user)
                listenForUpdates(userId)
            }
        }
    }

    private fun listenForUpdates(userId: String) {
        viewModelScope.launch {
            userRepository.listenForUpdates(userId).collect {
                _profileState.value = _profileState.value.copy(user = it)
            }
        }
    }

    fun saveForm() {
        val updatedUser = _profileState.value.user.copy()
        updatedUser.updatedAt = System.currentTimeMillis()
        val image = _profileState.value.imageUri

        _profileState.value = _profileState.value.copy(isLoading = true)

        if (image != null) {
            viewModelScope.launch {
                val imageUrl = userRepository.uploadProfileImage(updatedUser.id, image)
                if (imageUrl != null) {
                    updatedUser.imageUrl = imageUrl
                    updateUser(updatedUser)
                } else {
                    _profileState.value = _profileState.value.copy(isLoading = false)
                    onEvent(
                        ProfileEvent.ShowMessage(
                            "Error al subir imagen de perfil",
                            SnackbarType.ERROR
                        )
                    )
                }
            }
        } else {
            updateUser(updatedUser)
        }
    }

    private fun updateUser(user: User) {
        viewModelScope.launch {
            val success = userRepository.updateUser(user)
            _profileState.value = _profileState.value.copy(isLoading = false)
            if (success) {
                onEvent(
                    ProfileEvent.ShowMessage(
                        "Perfil actualizado correctamente",
                        SnackbarType.SUCCESS
                    )
                )
            } else {
                onEvent(ProfileEvent.ShowMessage("Error al actualizar perfil", SnackbarType.ERROR))
            }
        }
    }
}