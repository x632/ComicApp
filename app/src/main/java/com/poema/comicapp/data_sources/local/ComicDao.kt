package com.poema.comicapp.data_sources.local


import androidx.lifecycle.LiveData
import androidx.room.*
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.data_sources.model.ComicPostCache
import com.poema.comicapp.data_sources.model.IsRead
import kotlinx.coroutines.flow.Flow


@Dao
interface ComicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comicPostCache: ComicPostCache):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comicLisItem: ComicListItem):Long

    @Query("DELETE FROM comics WHERE num = :id")
    suspend fun deleteComicPostCachedById(id: Int)

    @Query("DELETE FROM comicListItems WHERE id = :id")
    suspend fun deleteComicListItemById(id: Int)

    @Query("SELECT * FROM comics WHERE num = :id" )
    suspend fun findComicPostCacheById(id: Int) : ComicPostCache

    @Query("SELECT * FROM comicListItems WHERE id = :id" )
    suspend fun findComicListItemById(id: Int) : ComicListItem

    @Query("SELECT * FROM comicListItems")
    fun observeComicListItems(): Flow<List<ComicListItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(isRead: IsRead):Long

    @Query("SELECT * FROM read")
    fun observeIsRead(): Flow<List<IsRead>>

}