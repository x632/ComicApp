package com.poema.comicapp.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.poema.comicapp.data_sources.local.LocalDataSource
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.data_sources.model.ComicPostCache
import com.poema.comicapp.data_sources.model.IsRead
import com.poema.comicapp.data_sources.remote.RemoteDataSource
import com.poema.comicapp.data_sources.repository.Repository
import com.poema.comicapp.other.ScrapingFunctions
import kotlinx.coroutines.flow.Flow
import java.net.URL
import javax.inject.Inject

class RepositoryImpl @Inject constructor(

    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource

) : Repository {

    override suspend fun deleteFavoriteById(id: Int) = localDataSource.comicDao.deleteComicListItemById(id)

    override fun observeFavorites() = localDataSource.comicDao.observeComicListItems()

    override suspend fun saveFavorite(comicListItem: ComicListItem) = localDataSource.comicDao.insert(comicListItem)

    override suspend fun saveComicPostCache(comicPostCache: ComicPostCache) = localDataSource.comicDao.insert(comicPostCache)

    override suspend fun deleteComicPostCacheById(id: Int) = localDataSource.comicDao.deleteComicPostCachedById(id)

    override suspend fun findComicPostCacheById(id: Int) = localDataSource.comicDao.findComicPostCacheById(id)

    override fun observeAllIsRead(): Flow<List<IsRead>> = localDataSource.comicDao.observeIsRead()

    override suspend fun saveIsRead(isRead: IsRead) =  localDataSource.comicDao.insert(isRead)

    override suspend fun getComicPost(id: Int) = remoteDataSource.api.getComicPost(id)

    override fun getArchive() : List<ComicListItem>? {
        val response = remoteDataSource.getArchive()
        return if(response != null){
            val str = response.body!!.string()
            val list = ScrapingFunctions.doScrape(str)
            list
        } else{
            null
        }
    }

    override fun getBitMap(url: String): Bitmap {
        val imageStream = URL(url).openConnection().getInputStream()
        val bitmap = Bitmap.createBitmap(BitmapFactory.decodeStream(imageStream))
        imageStream.close()
        return bitmap
    }
}
