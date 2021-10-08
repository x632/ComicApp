package com.poema.comicapp.repository

import android.graphics.Bitmap
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.model.ComicPostCache
import com.poema.comicapp.model.IsRead
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


interface Repository {

    suspend fun getComicPost(id: Int): Response<ComicPost>

    fun getArchive() : List<ComicListItem>?

    suspend fun deleteFavoriteById(id: Int)

    suspend fun getFavorites() : List<ComicListItem>

    suspend fun saveFavorite(comicListItem: ComicListItem): Long

    suspend fun saveComicPostCache(comicPostCache: ComicPostCache):Long

    suspend fun deleteComicPostCacheById(id: Int)

    suspend fun findComicPostCacheById(id: Int) : ComicPostCache

    fun getBitMap(url: String): Bitmap

    fun observeAllIsRead(): Flow<List<IsRead>>

    suspend fun saveIsRead(isRead: IsRead):Long
}

