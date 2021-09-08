package com.poema.comicapp.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName= "comics")
class ComicPostCache  (
        val month: String,
        @PrimaryKey(autoGenerate = false)
        val num: Int,
        val link: String,
        val year: String,
        val news: String,
        val safe_title: String,
        val transcript: String,
        val alt : String,
        val img : String,
        val title: String,
        val day: String,
        var imgBitMap: Bitmap? = null,
    )
