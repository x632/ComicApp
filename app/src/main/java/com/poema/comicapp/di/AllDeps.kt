package com.poema.comicapp.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.poema.comicapp.api.PostApi
import com.poema.comicapp.data_sources.DataSources
import com.poema.comicapp.data_sources.local.ComicDao
import com.poema.comicapp.data_sources.local.LocalDataSource
import com.poema.comicapp.data_sources.local.db.ComicDatabase
import com.poema.comicapp.data_sources.remote.RemoteDataSource
import com.poema.comicapp.other.Constants
import com.poema.comicapp.repository.Repository
import com.poema.comicapp.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AllDeps {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl("https://xkcd.com/")
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideComicApi(retrofit: Retrofit.Builder): PostApi {
        return retrofit
            .build()
            .create(PostApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRequest(): Request.Builder {
        return Request.Builder()
            .url(Constants.ARCHIVE_URL)

    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(2000, TimeUnit.MILLISECONDS)
            .readTimeout(2000, TimeUnit.MILLISECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideComicDb(@ApplicationContext context: Context): ComicDatabase {
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

    @Singleton
    @Provides
    fun provideRepository(
        comicDao: ComicDao,
        okHttpClient: OkHttpClient,
        request: Request.Builder,
        api: PostApi,
    ): Repository {
        return RepositoryImpl(
            DataSources(
                local = LocalDataSource(comicDao = comicDao),
                remote = RemoteDataSource(request = request, api = api,client = okHttpClient)
            )
        )
    }
}