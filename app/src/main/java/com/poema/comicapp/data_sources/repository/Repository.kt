package com.poema.comicapp.data_sources.repository

import android.graphics.Bitmap
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.data_sources.model.ComicPostDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response


interface Repository {

    suspend fun getComicPost(id: Int): Response<ComicPostDto>

    suspend fun getArchive() : List<ComicListItem>?

    suspend fun deleteFavoriteById(id: Int)

    fun observeCache() : Flow<List<ComicListItem>>

    suspend fun saveComicListItem(comicListItem: ComicListItem): Long

    fun getBitMap(url: String): Bitmap

    suspend fun findComicListItemById(id:Int) : ComicListItem

    suspend fun update(isFav:Boolean,id: Int):Int

}

