package com.poema.comicapp.data_sources.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.poema.comicapp.data_sources.local.ComicDao
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPostCache
import com.poema.comicapp.model.IsRead

@Database(entities = [ComicPostCache::class,ComicListItem::class, IsRead::class], version = 16)
@TypeConverters(Converters::class)
abstract class ComicDatabase: RoomDatabase(){

    abstract fun comicDao(): ComicDao

    companion object{
        const val DATABASE_NAME: String = "comic_db"
    }
}