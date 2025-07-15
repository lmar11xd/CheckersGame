package com.lmar.checkersgame.domain.usecase.single_game

import kotlinx.coroutines.delay
import javax.inject.Inject

class StartGameTimerUseCase @Inject constructor() {
    suspend operator fun invoke(
        updateTimer: (Int) -> Unit,
        shouldContinue: () -> Boolean
    ) {
        var time = 0
        while (shouldContinue()) {
            delay(1000)
            time++
            updateTimer(time)
        }
    }
}
