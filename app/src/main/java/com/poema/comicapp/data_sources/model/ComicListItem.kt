package com.poema.comicapp.data_sources.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "comicListItems")
class ComicListItem(
    val title: String,
    @PrimaryKey(autoGenerate = false) val id: Int,
    val date: String,
   /* val alt : String? = null,
    val bitmap : Bitmap? = null,*/
    var isFavourite: Boolean,
    var isNew: Boolean,
    var isRead: Boolean,
)




