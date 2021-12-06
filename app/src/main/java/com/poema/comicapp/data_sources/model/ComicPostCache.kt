package com.poema.comicapp.data_sources.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName= "comics")
class ComicPostCache  (
        @PrimaryKey(autoGenerate = false)
        val num: Int,
        val alt : String,
        val title: String,
        var imgBitMap: Bitmap? = null,
    )
