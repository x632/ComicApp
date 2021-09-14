package com.poema.comicapp.ui.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.poema.comicapp.R
import com.poema.comicapp.model.*
import com.poema.comicapp.model.GlobalList.globalList
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    lateinit var viewModel: DetailViewModel
    lateinit var titleHolder: TextView
    lateinit var altHolder: TextView
    lateinit var imageHolder: ImageView
    lateinit var progBarHolder: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val internetConnection = this.isInternetAvailable()

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        altHolder = findViewById<TextView>(R.id.tvAlt)
        titleHolder = findViewById(R.id.textView)
        imageHolder = findViewById<ImageView>(R.id.imageView)
        progBarHolder = findViewById<ProgressBar>(R.id.progressBar2)
        val heartHolder = findViewById<ImageView>(R.id.heartHolder)

        val explBtn = findViewById<Button>(R.id.btnWeb)

        viewModel.number = intent.getIntExtra("id", 0)
        viewModel.index = viewModel.indexInList(viewModel.number)
        if (internetConnection) {
            viewModel.getComicPost(viewModel.number)
        } else {
            if (viewModel.isInCache(viewModel.number)) {
                viewModel.getComicPostCache(viewModel.number)
                subscribeToComicPostCache()
            }
        }

        val heart = ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_favorite_48,null)
        val emptyHeart = ResourcesCompat.getDrawable(resources,R.drawable.ic_baseline_favorite_border_48,null)
        if (viewModel.isInCache(viewModel.number)) heartHolder.setImageDrawable(heart)

        viewModel.getResponse().observe(this, {

            if (it.isSuccessful) {
                titleHolder.text = it.body()?.title
                Glide.with(this)
                    .load(it.body()?.img)
                    .into(imageHolder)
                it.body()?.let { post ->
                    viewModel.createBitmap(post.img)
                    viewModel.postFromInternet = post
                    altHolder.text = post.alt
                }
                progBarHolder.visibility = View.GONE
            }
        })

        subscribeToFinishedBitmap()

        heartHolder.setOnClickListener {
            if (viewModel.cachedPostIsInitialized) {
                if (!viewModel.isInCache(viewModel.number)) {
                    globalList[viewModel.index!!].isFavourite = true
                    viewModel.saveComicPostCache(viewModel.cachedPost!!)
                    viewModel.saveComicListItem(viewModel.comicListItem!!)
                    heartHolder.setImageDrawable(heart)
                    //showToast("has been saved to favorites!")
                } else {
                    globalList[viewModel.index!!].isFavourite = false
                    heartHolder.setImageDrawable(emptyHeart)
                    viewModel.deleteComicPostCacheById(viewModel.number)
                    viewModel.deleteComicListItemById(viewModel.number)

                    //showToast("has been deleted from favorites!")
                }
            }
        }

        explBtn.setOnClickListener {
            if (internetConnection) {
                val intent = Intent(this, ExplanationActivity::class.java)
                intent.putExtra("id", viewModel.number)
                intent.putExtra("title", viewModel.comicListItem!!.title)
                this.startActivity(intent)
            } else {
                showToast("You cannot see explanations without internet-connection. Please check your connection!")
            }
        }
    }

    private fun subscribeToComicPostCache() {
        viewModel.comicPostCache.observe(this) {

            titleHolder.text = it.title
            Glide.with(this).load(it.imgBitMap).into(imageHolder)
            altHolder.text = it.alt
            progBarHolder.visibility = View.GONE

        }
    }

    private fun subscribeToFinishedBitmap() {
        viewModel.getLiveBitMap().observe(this) {
            viewModel.cachedPost = ComicPostCache(
                viewModel.postFromInternet!!.month,
                viewModel.postFromInternet!!.num,
                viewModel.postFromInternet!!.link,
                viewModel.postFromInternet!!.year,
                viewModel.postFromInternet!!.news,
                viewModel.postFromInternet!!.safe_title,
                viewModel.postFromInternet!!.transcript,
                viewModel.postFromInternet!!.alt,
                viewModel.postFromInternet!!.img,
                viewModel.postFromInternet!!.title,
                viewModel.postFromInternet!!.day,
                it,
            )
            viewModel.cachedPostIsInitialized = true

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this, message,
            Toast.LENGTH_SHORT
        ).show()
    }

}