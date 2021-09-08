package com.poema.comicapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.model.ComicPostCache


@Dao
interface ComicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comicPostCache: ComicPostCache):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comicLisItem: ComicListItem):Long

    @Delete
    suspend fun delete(comicListItem: ComicListItem)

    @Query("SELECT * FROM comics")
    suspend fun getAllCachedComicPosts(): List<ComicPostCache>

    @Query("SELECT * FROM comics WHERE num = :id" )
    suspend fun findComicPostCacheById(id: Int) : ComicPostCache

    @Query("SELECT * FROM comicListItems WHERE id = :id" )
    suspend fun findComicListItemById(id: Int) : ComicListItem

    @Query("SELECT * FROM comicListItems")
    suspend fun getAllComicListItems(): List<ComicListItem>

}