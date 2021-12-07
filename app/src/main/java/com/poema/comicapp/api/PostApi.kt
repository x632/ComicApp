package com.poema.comicapp.api

import com.poema.comicapp.data_sources.model.ComicPostDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApi {
    @GET("{id}/info.0.json")
    suspend fun getComicPost(@Path("id") postId:Int): Response<ComicPostDto>


}

/*Retrofit retrofit = new Retrofit.Builder()
.baseUrl(BASE_URL)
.addConverterFactory(ScalarsConverterFactory.create())*/
