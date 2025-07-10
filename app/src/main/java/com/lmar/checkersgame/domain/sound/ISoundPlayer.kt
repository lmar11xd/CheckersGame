package com.lmar.checkersgame.domain.sound

interface ISoundPlayer {
    fun playMove()
    fun playCapture()
    fun playCrown()
    fun playWin()
    fun playDraw()
    fun playLose()
    fun playClick()
}