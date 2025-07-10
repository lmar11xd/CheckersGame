package com.lmar.checkersgame.core.di

import com.lmar.checkersgame.domain.repository.common.IAuthRepository
import com.lmar.checkersgame.domain.repository.common.IUserRepository
import com.lmar.checkersgame.data.common.FirebaseAuthRepository
import com.lmar.checkersgame.data.common.FirebaseUserRepository
import com.lmar.checkersgame.data.repository.FirebaseGameRepository
import com.lmar.checkersgame.domain.repository.IGameRepository
import com.lmar.checkersgame.domain.repository.IRoomRepository
import com.lmar.checkersgame.data.repository.FirebaseRoomRepository
import com.lmar.checkersgame.domain.ai.AIPlayer
import com.lmar.checkersgame.domain.ai.Difficulty
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): IAuthRepository {
        return FirebaseAuthRepository()
    }

    @Provides
    @Singleton
    fun provideUserRepository(): IUserRepository {
        return FirebaseUserRepository()
    }

    @Provides
    @Singleton
    fun provideGameRepository(): IGameRepository {
        return FirebaseGameRepository()
    }

    @Provides
    @Singleton
    fun provideRoomRepository(): IRoomRepository {
        return FirebaseRoomRepository()
    }

    @Provides
    @Singleton
    fun aiPlayerRepository(): AIPlayer {
        return AIPlayer(Difficulty.EASY)
    }
}