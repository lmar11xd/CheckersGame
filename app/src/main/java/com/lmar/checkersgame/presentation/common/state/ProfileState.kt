package com.lmar.checkersgame.presentation.common.state

import android.net.Uri
import com.lmar.checkersgame.domain.model.User

data class ProfileState (
    val user: User = User(),
    val imageUri: Uri? = null,
    val isShowingForm: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
)