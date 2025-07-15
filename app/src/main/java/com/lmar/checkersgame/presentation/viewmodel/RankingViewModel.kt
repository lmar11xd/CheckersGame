package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.domain.model.User
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.ui.event.RankingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _topPlayers = MutableStateFlow<List<User>>(emptyList())
    val topPlayers: StateFlow<List<User>> = _topPlayers

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            val users = userRepository.getTopPlayers(10)
            _topPlayers.value = users
        }
    }

    fun onEvent(event: RankingEvent) {
        when (event) {
            RankingEvent.ToBack -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToBack)
                }
            }
        }
    }
}