package com.poema.comicapp.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.poema.comicapp.api.PostApi
import com.poema.comicapp.db.ComicDao
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.model.ComicPostCache
import com.poema.comicapp.other.Constants.ARCHIVE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import java.net.URL
import javax.inject.Inject


class Repository @Inject constructor(
    private val api: PostApi,
    private val comicDao: ComicDao,
) {

    suspend fun getComicPost(id: Int): Response<ComicPost> = api.getComicPost(id)

    suspend fun deleteFavoriteById(id: Int) = comicDao.deleteComicListItemById(id)

    suspend fun getFavorites(): List<ComicListItem> = comicDao.getAllComicListItems()

    suspend fun saveFavorite(comicListItem: ComicListItem) = comicDao.insert(comicListItem)

    suspend fun saveComicPostCache(comicPostCache: ComicPostCache) = comicDao.insert(comicPostCache)

    suspend fun deleteComicPostCacheById(id: Int) = comicDao.deleteComicPostCachedById(id)

    suspend fun findComicPostCacheById(id: Int) = comicDao.findComicPostCacheById(id)
    
    fun getArchiveAsString():okhttp3.Response{
            val request = Request.Builder()
                .url(ARCHIVE_URL)
                .build()
            return OkHttpClient().newCall(request).execute()
    }

   fun getBitMap(url: String):Bitmap {
            val imageStream = URL(url).openConnection().getInputStream()
            return Bitmap.createBitmap(BitmapFactory.decodeStream(imageStream))
    }




}