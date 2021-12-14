package com.poema.comicapp.data_sources.model

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.poema.comicapp.R

@BindingAdapter("android:setBitmap")
fun setBitmap(imageView: ImageView, item: ComicListItem) {
    if (item.bitmap != null) {
        imageView.setImageBitmap(item.bitmap!!)
    } else {
        imageView.setImageResource(R.drawable.ic_launcher_foreground2)
    }
}

@BindingAdapter("android:setHeart")
fun setHeart(imageView: ImageView, isFavourite: Boolean) {
    if (!isFavourite) {
        imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
    } else {
        imageView.setImageResource(R.drawable.ic_baseline_favorite_24)
    }

}

@BindingAdapter("android:setIsNew")
fun setIsNew(imageView: ImageView, isNew: Boolean) {
    if (isNew) imageView.visibility =
        View.VISIBLE
    else imageView.visibility = View.GONE
}


@Entity(tableName = "comicListItems")
class ComicListItem(
    val title: String,
    @PrimaryKey(autoGenerate = false) val id: Int,
    val date: String,
    var alt: String? = null,
    var bitmap: Bitmap? = null,
    var isFavourite: Boolean,
    var isNew: Boolean,
)








