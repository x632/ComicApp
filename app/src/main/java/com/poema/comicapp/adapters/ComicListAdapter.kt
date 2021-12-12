package com.poema.comicapp.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.R.*
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.poema.comicapp.databinding.ComicItemLayoutBinding
import com.poema.comicapp.ui.fragments.HomeFragmentDirections


class ComicListAdapter(private val context: Context) :
    RecyclerView.Adapter<ComicListAdapter.ComicItemViewHolder>() {

    private lateinit var comicList: List<ComicListItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicItemViewHolder {

        val layoutInflater = LayoutInflater.from(context)
        val comicItemLayoutBinding = ComicItemLayoutBinding.inflate(layoutInflater, parent, false)
        return ComicItemViewHolder(comicItemLayoutBinding)
    }

    override fun onBindViewHolder(holder: ComicItemViewHolder, position: Int) {
        holder.bind(comicList[position])
    }

    override fun getItemCount(): Int {
        return comicList.size
    }

    fun submitList(list: MutableList<ComicListItem>) {
        comicList = list
        notifyDataSetChanged()
    }

    inner class ComicItemViewHolder(private val binding: ComicItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ComicListItem) {
            binding.comicListItem = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(item.id)
                Navigation.findNavController(it).navigate(action)
            }

        }
    }
}