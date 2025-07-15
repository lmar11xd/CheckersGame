package com.lmar.checkersgame.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lmar.checkersgame.presentation.common.event.UiEvent
import com.lmar.checkersgame.presentation.ui.event.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.ToProfile -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToProfile)
                }
            }

            HomeEvent.ToRanking -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToRanking)
                }
            }

            HomeEvent.ToRoom -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToRoom)
                }
            }

            HomeEvent.ToSettings -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToSettings)
                }
            }

            is HomeEvent.ToSingleGame -> {
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ToSingleGame(event.level))
                }
            }
        }
    }
}