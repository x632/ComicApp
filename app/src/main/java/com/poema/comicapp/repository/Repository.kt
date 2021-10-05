package com.poema.comicapp.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.poema.comicapp.data_sources.DataSources
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.model.ComicPostCache
import retrofit2.Response
import java.net.URL
import javax.inject.Inject


class Repository @Inject constructor(
    private val dataSources: DataSources
) {

    suspend fun getComicPost(id: Int): Response<ComicPost> = dataSources.remote.api.getComicPost(id)

    fun getArchiveAsString() = dataSources.remote.getArchive()

    suspend fun deleteFavoriteById(id: Int) = dataSources.local.comicDao.deleteComicListItemById(id)

    suspend fun getFavorites(): List<ComicListItem> =
        dataSources.local.comicDao.getAllComicListItems()

    suspend fun saveFavorite(comicListItem: ComicListItem) =
        dataSources.local.comicDao.insert(comicListItem)

    suspend fun saveComicPostCache(comicPostCache: ComicPostCache) =
        dataSources.local.comicDao.insert(comicPostCache)

    suspend fun deleteComicPostCacheById(id: Int) =
        dataSources.local.comicDao.deleteComicPostCachedById(id)

    suspend fun findComicPostCacheById(id: Int) =
        dataSources.local.comicDao.findComicPostCacheById(id)

    fun getBitMap(url: String): Bitmap {
        val imageStream = URL(url).openConnection().getInputStream()
        return Bitmap.createBitmap(BitmapFactory.decodeStream(imageStream))
    }

}