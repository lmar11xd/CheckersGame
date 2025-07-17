package com.lmar.checkersgame.domain.usecase

import javax.inject.Inject

class StopGameTimerUseCase @Inject constructor() {
    operator fun invoke(stopJob: () -> Unit) = stopJob()
}