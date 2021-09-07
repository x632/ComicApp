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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.poema.comicapp.R
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.ui.activities.DetailActivity
import android.graphics.drawable.Drawable
import com.poema.comicapp.R.*


class ComicListAdapter(private val context: Context) :
    RecyclerView.Adapter<ComicListAdapter.ComicItemViewHolder>() {

    private var uid = ""
    private lateinit var comicList: List<ComicListItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicItemViewHolder {
        return ComicItemViewHolder(
            LayoutInflater.from(context).inflate(layout.comic_item_layout, parent, false)
        )


    }

    override fun onBindViewHolder(holder: ComicItemViewHolder, position: Int) {
        val comicListItem = comicList[position]
        holder.tv1.text = comicListItem.date
        holder.tv2.text = comicListItem.title
        holder.tv3.text = comicListItem.id
        holder.itemView.setOnClickListener {
            println("!!! Har klickat på ${comicList[position].date}")
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("pos", comicListItem.id.toInt())
            context.startActivity(intent)
        }
        if (!comicListItem.isFavourite) {
            holder.ivheart.setImageResource(drawable.ic_baseline_favorite_border_24)
        } else {
            holder.ivheart.setImageResource(drawable.ic_baseline_favorite_24)
        }
    }

    override fun getItemCount(): Int {
        return comicList.size
    }

    fun submitList(list: MutableList<ComicListItem>) {
        comicList = list
    }


    inner class ComicItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv1: TextView = itemView.findViewById(id.tvTitle)
        val tv2: TextView = itemView.findViewById(id.tvDate)
        val tv3: TextView = itemView.findViewById(id.tvNumber)
        val ivheart: ImageView = itemView.findViewById(id.iv_heart)


        /* init {
                 itemView.setOnClickListener {
                     println("!!! Har klickat på ${comicList[adapterPosition].date}")
                     val bundle = Bundle()
                     bundle.putInt("pos", adapterPosition)
                     context.arguments = bundle
                     findNavController(context).navigate(R.id.action_FirstFragment_to_SecondFragment)

                 }
             }*/
        /*val itemImage: ImageView = itemView.findViewById(R.id.item_image)
            var itemTitle: TextView = itemView.findViewById(R.id.textView5)
            private val itemDeleteImage: ImageView = itemView.findViewById(R.id.deleteImageView)*/

        /*  init {

                itemView.setOnClickListener {
                    if(context.isInternetAvailable()) {
                        val video = comicListItem[absoluteAdapterPosition]
                        val intent = Intent(context, ShowVideo::class.java)
                        intent.putExtra("title", video.title)
                        intent.putExtra("url", video.url)
                        intent.putExtra("docId", video.docId)
                        context.startActivity(intent)
                    }
                    else{
                        val msg="the YouTube-stream is not cached, check your internet-connection"
                        //showToast(msg)
                    }
                }

               itemDeleteImage.setOnClickListener{ view ->
                    if(context.isInternetAvailable()) {
                        val video = categoryItem[absoluteAdapterPosition]
                        val dialogBuilder = AlertDialog.Builder(context)

                        dialogBuilder.setTitle("Remove video")
                            .setMessage("Are you sure you want to remove this video?")
                            //.setCancelable(false)

                            .setIcon(0)
                            .setPositiveButton("Remove video") { _, _ ->
                                deleteVideo(video)
                                Snackbar.make(view, "Video removed", Snackbar.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.cancel()
                            }

                        val alert = dialogBuilder.create()

                        alert.show()
                    }
                    else{
                        val msg="Your online cloud database has priority, therefore deleting can only be made while online"
                        showToast(msg)
                    }
                }
            }*/
    }
}