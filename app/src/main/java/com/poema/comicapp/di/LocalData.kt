package com.poema.comicapp.di

import android.content.Context
import androidx.room.Room
import com.poema.comicapp.data_sources.local.ComicDao
import com.poema.comicapp.data_sources.local.db.ComicDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalData {

    @Singleton
    @Provides
    fun provideComicDb(@ApplicationContext context: Context) : ComicDatabase {
        return Room.databaseBuilder(
            context,
            ComicDatabase::class.java,
            ComicDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Singleton
    @Provides
    fun provideComicDAO(comicDatabase: ComicDatabase): ComicDao {
        return comicDatabase.comicDao()

    }



}