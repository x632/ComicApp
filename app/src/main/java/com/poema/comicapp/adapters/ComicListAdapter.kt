package com.poema.comicapp.adapters

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.poema.comicapp.R
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.ui.activities.DetailActivity
import com.poema.comicapp.R.*
import com.poema.comicapp.databinding.ComicItemLayoutBinding


class ComicListAdapter(private val context: Context) :
    RecyclerView.Adapter<ComicListAdapter.ComicItemViewHolder>() {

    private var uid = ""
    private lateinit var comicList: List<ComicListItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicItemViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val comicItemLayoutBinding : ComicItemLayoutBinding = ComicItemLayoutBinding.inflate(layoutInflater, parent, false)
        return ComicItemViewHolder(comicItemLayoutBinding)
    }

    override fun onBindViewHolder(holder: ComicItemViewHolder, position: Int) {
        val comicListItem = comicList[position]
        holder.bind(comicListItem)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("id", comicListItem.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return comicList.size
    }

    fun submitList(list: MutableList<ComicListItem>) {
        comicList = list

    }


    inner class ComicItemViewHolder(val binding: ComicItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comicListItem: ComicListItem){
            binding.comicListItem = comicListItem
        }


    }
}

@BindingAdapter("android:setDrawable")
fun setDrawable(imageView: ImageView, isFavourite: Boolean) {
    if (isFavourite) {
        imageView.setImageResource(R.drawable.ic_baseline_favorite_24)
    } else {
        imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
    }
}