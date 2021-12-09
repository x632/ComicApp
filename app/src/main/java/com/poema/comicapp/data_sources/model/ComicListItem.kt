package com.poema.comicapp.data_sources.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "comicListItems")
class ComicListItem(
    val title: String,
    @PrimaryKey(autoGenerate = false) val id: Int,
    val date: String,
    var alt : String? = null,
    var bitmap: Bitmap? = null,
    var isFavourite: Boolean,
    var isNew: Boolean,
)




