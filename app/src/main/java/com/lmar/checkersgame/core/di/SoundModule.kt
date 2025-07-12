package com.lmar.checkersgame.core.di

import android.content.Context
import com.lmar.checkersgame.data.sound.SoundPlayer
import com.lmar.checkersgame.data.sound.SoundPlayerWrapper
import com.lmar.checkersgame.domain.sound.ISoundPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SoundModule {

    @Provides
    @Singleton
    fun provideSoundPlayer(@ApplicationContext context: Context): ISoundPlayer {
        return SoundPlayer(context)
    }

    @Provides
    @Singleton
    fun provideSoundPlayerWrapper(soundPlayer: ISoundPlayer): SoundPlayerWrapper {
        return SoundPlayerWrapper(soundPlayer)
    }
}