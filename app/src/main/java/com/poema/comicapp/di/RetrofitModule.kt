package com.poema.comicapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.poema.comicapp.api.PostApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson:Gson): Retrofit.Builder{
        return Retrofit.Builder()
            .baseUrl("https://xkcd.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
    }
    @Singleton
    @Provides
    fun provideComicApi(retrofit:Retrofit.Builder) : PostApi{
        return retrofit
            .build()
            .create(PostApi::class.java)
    }
}


