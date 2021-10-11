package com.poema.comicapp.data_sources.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "comicListItems")
class ComicListItem(
    val title: String,
    @PrimaryKey(autoGenerate = false) val id: Int,
    val date: String,
    var isFavourite: Boolean,
    var isNew: Boolean,
    var isRead: Boolean = false,
)




