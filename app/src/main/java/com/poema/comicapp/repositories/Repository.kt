package com.poema.comicapp.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.poema.comicapp.api.PostApi
import com.poema.comicapp.db.ComicDao
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.model.ComicPostCache
import com.poema.comicapp.other.Constants.ARCHIVE_URL
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import retrofit2.Response
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject


class Repository @Inject constructor(
    private val api: PostApi,
    private val comicDao: ComicDao
) {

    private val liveString = MutableLiveData<String>()

    val bitmap = MutableLiveData<Bitmap>()


    suspend fun getComicPost(id: Int): Response<ComicPost> {
        return api.getComicPost(id)
    }


    fun getArchiveAsString() {

        CoroutineScope(IO).launch {

            val request = Request.Builder()
                .url(ARCHIVE_URL)
                .build()
            //är inom coroutine, kan vara blocking..
            OkHttpClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str = response.body!!.string()

                withContext(Main) {
                    liveString.value = str
                }
            }
        }
    }


    fun getBitMap(url: String) {

        CoroutineScope(IO).launch {

            val imageS = URL(url).openConnection().getInputStream()
            val themap = Bitmap.createBitmap(BitmapFactory.decodeStream(imageS))
            withContext(Main) {
                bitmap.value = themap
            }
            imageS.close()
        }
    }

    suspend fun deleteComicPostCacheById(id:Int) = comicDao.deleteComicPostCachedById(id.toLong())

    suspend fun deleteComicListItemById(id:Int) = comicDao.deleteComicListItemById(id.toLong())

    suspend fun getFavorites(): List<ComicListItem> = comicDao.getAllComicListItems()

    suspend fun saveComicPostCache(comicPostCache: ComicPostCache) = comicDao.insert(comicPostCache)

    suspend fun saveComicListItem(comicListItem: ComicListItem) = comicDao.insert(comicListItem)

    suspend fun findComicPostById(id: Int) = comicDao.findComicPostCacheById(id)

    fun getLiveString(): MutableLiveData<String> {
        return liveString
    }



}