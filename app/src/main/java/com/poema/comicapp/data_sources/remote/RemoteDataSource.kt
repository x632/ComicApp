package com.poema.comicapp.data_sources.remote

import com.poema.comicapp.api.PostApi
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val client: OkHttpClient,
    private val request: Request.Builder,
    val api: PostApi) {


    fun getArchive():Response?{
        return try {
            client.newCall(request.build()).execute();

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}