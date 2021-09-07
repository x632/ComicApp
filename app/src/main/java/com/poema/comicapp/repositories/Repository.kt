package com.poema.comicapp.repositories

import androidx.lifecycle.MutableLiveData
import com.poema.comicapp.api.PostApi
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.other.Constants.ARCHIVE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject


class Repository @Inject constructor(
    private val api: PostApi
){

    private val liveString = MutableLiveData<String>()

    suspend fun getComicPost(id:Int): Response<ComicPost> {
        return api.getComicPost(id)
    }


    fun getArchiveAsString() {
        println("!!! FROM REPO : EXECUTED STRING GET ${System.currentTimeMillis()/100}")


        CoroutineScope(Dispatchers.IO).launch {

            val request = Request.Builder()
                .url(ARCHIVE_URL)
                .build()
            //är inom coroutine, kan vara blocking..
            OkHttpClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str = response.body!!.string()

                withContext(Dispatchers.Main) {
                    println("!!! FROM REPO ${System.currentTimeMillis()/100}")
                    liveString.value = str

                }
            }
        }

    }
    fun getLiveString(): MutableLiveData<String> {
        return liveString
    }
}