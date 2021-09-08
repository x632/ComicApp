package com.poema.comicapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName= "comicListItems")
class ComicListItem (
    val title: String,
    @PrimaryKey(autoGenerate = false)val id: Int,
    val date: String,
    var isFavourite: Boolean
)