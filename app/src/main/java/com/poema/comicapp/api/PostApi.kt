package com.poema.comicapp.api

import com.poema.comicapp.model.ComicPost
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface PostApi {
    @GET("{id}/info.0.json")
    suspend fun getComicPost(@Path("id") postId:Int): Response<ComicPost>

   /* @GET
    suspend fun getAsString(@Url url:String): Call<String>*/
}

