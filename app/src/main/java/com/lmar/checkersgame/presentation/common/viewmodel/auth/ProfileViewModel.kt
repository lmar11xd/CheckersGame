package com.lmar.checkersgame.presentation.common.viewmodel.auth

import android.net.Uri
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
class ProfileViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository
): ViewModel() {

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    private val _userState = MutableLiveData<User>()
    val userState: MutableLiveData<User> = _userState

    private val _profileImageUri = MutableLiveData<Uri?>(null)
    val profileImageUri: MutableLiveData<Uri?> = _profileImageUri

    private val _showForm = MutableLiveData(false)
    val showForm: LiveData<Boolean> = _showForm

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        authRepository.getCurrentUser()?.let{
            getUserById(it.uid)
        }
    }

    private fun getUserById(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            userRepository.getUserById(userId) { user ->
                _isLoading.value = false
                user?.let {
                    _userState.value = it
                    listenForUpdates(it.id)
                }
            }
        }
    }

    private fun listenForUpdates(userId: String) {
        userRepository.listenForUpdates(userId) { user ->
            _userState.value = user
        }
    }

    fun setProfileImage(uri: Uri?) {
        profileImageUri.value = uri
    }

    fun changeName(value: String) {
        if(value.isEmpty()) return
        val currentUser = _userState.value ?: return
        _userState.value = currentUser.copy(
            names = value
        )
    }

    fun showForm() {
        _showForm.value = true
    }

    fun dismissForm() {
        _showForm.value = false
    }

    fun saveForm() {
        val updatedUser = _userState.value ?: return
        updatedUser.updatedAt = System.currentTimeMillis()
        val image = _profileImageUri.value

        _isLoading.value = true
        if (image != null) {
            viewModelScope.launch {
                userRepository.uploadProfileImage(updatedUser.id, image) { success, url ->
                    if (success && url != null) {
                        updatedUser.imageUrl = url
                    }
                    updateUser(updatedUser)
                }
            }
        } else {
            updateUser(updatedUser)
        }
    }

    private fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user) { success ->
                _isLoading.value = false
                if (success) dismissForm()
            }
        }
    }
}