package com.poema.comicapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.ui.activities.DetailActivity
import com.poema.comicapp.R.*


class ComicListAdapter(private val context: Context) :
    RecyclerView.Adapter<ComicListAdapter.ComicItemViewHolder>() {

    private lateinit var comicList: List<ComicListItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicItemViewHolder {
        return ComicItemViewHolder(
            LayoutInflater.from(context).inflate(layout.comic_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ComicItemViewHolder, position: Int) {
        val comicListItem = comicList[position]
        holder.tv1.text = comicListItem.title
        holder.tv2.text = comicListItem.date
        holder.tv3.text = comicListItem.id.toString()
        holder.itemView.setOnClickListener {

            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("id", comicListItem.id)
            context.startActivity(intent)
        }
        if (!comicListItem.isFavourite) {
            holder.ivheart.setImageResource(drawable.ic_baseline_favorite_border_24)
        } else {
            holder.ivheart.setImageResource(drawable.ic_baseline_favorite_24)
        }
        if(comicListItem.isNew) holder.ivNew.visibility = View.VISIBLE else holder.ivNew.visibility = View.GONE

    }
    override fun getItemCount(): Int {
        return comicList.size
    }

    fun submitList(list: MutableList<ComicListItem>) {
        comicList = list
        notifyDataSetChanged()
    }


    inner class ComicItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv1: TextView = itemView.findViewById(id.tvTitle)
        val tv2: TextView = itemView.findViewById(id.tvDate)
        val tv3: TextView = itemView.findViewById(id.tvNumber)
        val ivheart: ImageView = itemView.findViewById(id.iv_heart)
        val ivNew: ImageView = itemView.findViewById(id.ivNew)
    }
}