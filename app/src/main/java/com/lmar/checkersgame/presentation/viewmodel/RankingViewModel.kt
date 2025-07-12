package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.model.User
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _topPlayers = MutableStateFlow<List<User>>(emptyList())
    val topPlayers: StateFlow<List<User>> = _topPlayers

    init {
        viewModelScope.launch {
            val users = userRepository.getTopPlayers(10)
            _topPlayers.value = users
        }
    }
}