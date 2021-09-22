package com.poema.comicapp.model

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.poema.comicapp.R


@Entity(tableName = "comicListItems")
class ComicListItem(
    val title: String,
    @PrimaryKey(autoGenerate = false) val id: Int,
    val date: String,
    var isFavourite: Boolean,
    var isNew: Boolean
)




