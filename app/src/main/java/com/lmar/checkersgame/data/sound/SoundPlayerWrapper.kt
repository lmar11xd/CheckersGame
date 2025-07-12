package com.lmar.checkersgame.data.sound

import com.lmar.checkersgame.domain.sound.ISoundPlayer

class SoundPlayerWrapper(
    private val soundPlayer: ISoundPlayer
) {
    fun playMove() = soundPlayer.playMove()
    fun playCapture() = soundPlayer.playCapture()
    fun playCrown() = soundPlayer.playCrown()
    fun playWin() = soundPlayer.playWin()
    fun playLose() = soundPlayer.playLose()
}
