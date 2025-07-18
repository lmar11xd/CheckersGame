package com.lmar.checkersgame.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lmar.checkersgame.core.utils.Constants
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
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Auth Instance
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    // Storage
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    // Refs

    @Provides
    @Singleton
    @Named("UserDatabaseRef")
    fun provideUserDatabaseRef(): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference(Constants.USERS_DATABASE)
    }

    @Provides
    @Singleton
    @Named("GameDatabaseRef")
    fun provideGameDatabaseRef(): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference("${Constants.DATABASE_REFERENCE}/${Constants.GAMES_REFERENCE}")
    }

    @Provides
    @Singleton
    @Named("RoomDatabaseRef")
    fun provideRoomDatabaseRef(): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference("${Constants.DATABASE_REFERENCE}/${Constants.ROOMS_REFERENCE}")
    }

    // Repositories

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): IAuthRepository {
        return FirebaseAuthRepository(auth)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        @Named("UserDatabaseRef") database: DatabaseReference,
        storage: FirebaseStorage
    ): IUserRepository {
        return FirebaseUserRepository(database, storage)
    }

    @Provides
    @Singleton
    fun provideGameRepository(
        @Named("GameDatabaseRef") database: DatabaseReference
    ): IGameRepository {
        return FirebaseGameRepository(database)
    }

    @Provides
    @Singleton
    fun provideRoomRepository(
        @Named("RoomDatabaseRef") database: DatabaseReference
    ): IRoomRepository {
        return FirebaseRoomRepository(database)
    }

    @Provides
    @Singleton
    fun aiPlayerRepository(): AIPlayer {
        return AIPlayer(Difficulty.EASY)
    }
}