package com.lmar.checkersgame.di

import com.lmar.checkersgame.data.common.IAuthRepository
import com.lmar.checkersgame.data.common.IUserRepository
import com.lmar.checkersgame.data.common.impl.FirebaseAuthRepository
import com.lmar.checkersgame.data.common.impl.FirebaseUserRepository
import com.lmar.checkersgame.data.repository.impl.FirebaseGameRepository
import com.lmar.checkersgame.data.repository.IGameRepository
import com.lmar.checkersgame.data.repository.IRoomRepository
import com.lmar.checkersgame.data.repository.impl.FirebaseRoomRepository
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
}