package com.poema.comicapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.model.ComicPostCache

@Database(entities = [ComicPostCache::class,ComicListItem::class], version = 11)
@TypeConverters(Converters::class)
abstract class ComicDatabase: RoomDatabase(){

    abstract fun comicDao(): ComicDao

    companion object{
        const val DATABASE_NAME: String = "comic_db"
    }
}