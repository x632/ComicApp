package com.poema.comicapp.data_sources.local


import androidx.room.*
import com.poema.comicapp.data_sources.model.ComicListItem
import kotlinx.coroutines.flow.Flow


@Dao
interface ComicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comicListItem: ComicListItem):Long

    @Query("DELETE FROM comicListItems WHERE id = :id")
    suspend fun deleteComicListItemById(id: Int)

    @Query("SELECT * FROM comicListItems WHERE id = :id" )
    suspend fun findComicListItemById(id: Int) : ComicListItem

    @Query("SELECT * FROM comicListItems ORDER BY id DESC")
    fun observeComicListItems(): Flow<List<ComicListItem>>

    @Query("SELECT * FROM comicListItems ORDER BY id DESC")
    suspend fun getComicListItems(): List<ComicListItem>

    @Query("UPDATE comicListItems SET isFavourite =:isFav WHERE id = :id")
    suspend fun update(isFav: Boolean,id: Int ):Int

}