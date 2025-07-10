package com.lmar.checkersgame.data.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.lmar.checkersgame.R
import com.lmar.checkersgame.domain.sound.ISoundPlayer
import javax.inject.Inject

class SoundPlayer @Inject constructor(context: Context): ISoundPlayer {
    private val soundPool: SoundPool
    private val winSound: Int
    private val drawSound: Int
    private val loseSound: Int
    private val clickSound: Int

    private val moveSound: Int
    private val captureSound: Int
    private val crownSound: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        winSound = soundPool.load(context, R.raw.winner_sound, 1)
        drawSound = soundPool.load(context, R.raw.draw_sound, 1)
        loseSound = soundPool.load(context, R.raw.loser_sound, 1)
        clickSound = soundPool.load(context, R.raw.screentap_sound, 1)
        moveSound = soundPool.load(context, R.raw.move_sound, 1)
        captureSound = soundPool.load(context, R.raw.capture_sound, 1)
        crownSound = soundPool.load(context, R.raw.crown_sound, 1)
    }

    override fun playMove() {
        soundPool.play(moveSound, 1f, 1f, 1, 0, 1f)
    }

    override fun playCapture() {
        soundPool.play(captureSound, 1f, 1f, 1, 0, 1f)
    }

    override fun playCrown() {
        soundPool.play(crownSound, 1f, 1f, 1, 0, 1f)
    }

    override fun playWin() {
        soundPool.play(winSound, 1f, 1f, 1, 0, 1f)
    }

    override fun playDraw() {
        soundPool.play(drawSound, 1f, 1f, 1, 0, 1f)
    }

    override fun playLose() {
        soundPool.play(loseSound, 1f, 1f, 1, 0, 1f)
    }

    override fun playClick() {
        soundPool.play(clickSound, 1f, 1f, 1, 0, 1f)
    }
}