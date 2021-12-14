package com.poema.comicapp.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.poema.comicapp.data_sources.local.LocalDataSource
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.data_sources.remote.RemoteDataSource
import com.poema.comicapp.data_sources.repository.Repository
import com.poema.comicapp.other.ScrapingFunctions
import java.net.URL
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : Repository {

    override suspend fun deleteFavoriteById(id: Int) = localDataSource.comicDao.deleteComicListItemById(id)

    override fun observeCache() = localDataSource.comicDao.observeComicListItems()

    override suspend fun saveComicListItem(comicListItem: ComicListItem) = localDataSource.comicDao.insert(comicListItem)

    override suspend fun getComicPostDto(id: Int) = remoteDataSource.api.getComicPost(id)

    override suspend fun getArchive() : List<ComicListItem>? {
        val response = remoteDataSource.getArchive()
        return if(response != null){
            val str = response.body!!.string()
            val list = ScrapingFunctions.doScrape(str)
            list
        } else {
            return localDataSource.comicDao.getComicListItems()
        }
    }

    override fun getBitMap(url: String): Bitmap {

        val imageStream = URL(url).openConnection().getInputStream()
        val bitmap = Bitmap.createBitmap(BitmapFactory.decodeStream(imageStream))
        imageStream.close()
        return bitmap
    }

    override suspend fun findComicListItemById(id:Int): ComicListItem = localDataSource.comicDao.findComicListItemById(id)

    override suspend fun update(isFav: Boolean, id: Int) = localDataSource.comicDao.update(isFav, id)

}
