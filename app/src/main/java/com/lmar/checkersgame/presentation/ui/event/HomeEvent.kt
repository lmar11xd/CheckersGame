package com.lmar.checkersgame.presentation.ui.event

import com.lmar.checkersgame.domain.ai.Difficulty

sealed class HomeEvent {
    object ToProfile: HomeEvent()
    object ToSettings: HomeEvent()
    data class ToSingleGame(val level: Difficulty): HomeEvent()
    object ToRanking: HomeEvent()
    object ToRoom: HomeEvent()
}