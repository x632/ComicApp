package com.poema.comicapp.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.poema.comicapp.data_sources.DataSources
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPostCache
import com.poema.comicapp.model.IsRead
import com.poema.comicapp.other.ScrapingFunctions
import kotlinx.coroutines.flow.Flow
import okhttp3.Response

import java.net.URL
import javax.inject.Inject



class RepositoryImpl @Inject constructor(

    private val dataSources: DataSources

) :Repository {

    override suspend fun getComicPost(id: Int) = dataSources.remote.api.getComicPost(id)

    override fun getArchive() : List<ComicListItem>? {
        val response = dataSources.remote.getArchive()
        return if(response != null){
            val str = response.body!!.string()
            val list = ScrapingFunctions.doScrape(str)
            list
        } else{
            null
        }
    }

    override suspend fun deleteFavoriteById(id: Int) = dataSources.local.comicDao.deleteComicListItemById(id)

    override suspend fun getFavorites() = dataSources.local.comicDao.getAllComicListItems()

    override suspend fun saveFavorite(comicListItem: ComicListItem) = dataSources.local.comicDao.insert(comicListItem)

    override suspend fun saveComicPostCache(comicPostCache: ComicPostCache) = dataSources.local.comicDao.insert(comicPostCache)

    override suspend fun deleteComicPostCacheById(id: Int) = dataSources.local.comicDao.deleteComicPostCachedById(id)

    override suspend fun findComicPostCacheById(id: Int) = dataSources.local.comicDao.findComicPostCacheById(id)

    override fun getBitMap(url: String): Bitmap {
        val imageStream = URL(url).openConnection().getInputStream()
        return Bitmap.createBitmap(BitmapFactory.decodeStream(imageStream))
    }

    override fun observeAllIsRead(): Flow<List<IsRead>> = dataSources.local.comicDao.observeIsRead()

    override suspend fun saveIsRead(isRead: IsRead) : Long{
       val a =  dataSources.local.comicDao.insert(isRead)
        println("!!! En isRead sparades med id:t: $a")
        return a
    }
}
