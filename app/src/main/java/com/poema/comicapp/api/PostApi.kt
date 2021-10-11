package com.poema.comicapp.api

import com.poema.comicapp.data_sources.model.ComicPost
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApi {
    @GET("{id}/info.0.json")
    suspend fun getComicPost(@Path("id") postId:Int): Response<ComicPost>

   /* @GET
    suspend fun getAsString(@Url url:String): Call<String>*/
}


