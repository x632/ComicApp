package com.poema.comicapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.poema.comicapp.R
import com.poema.comicapp.model.*
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    lateinit var cachedPost: ComicPostCache
    lateinit var comicListItem: ComicListItem
    lateinit var viewModel: DetailViewModel
    lateinit var postFromInternet: ComicPost
    lateinit var titleHolder: TextView
    lateinit var imageHolder: ImageView
    lateinit var progBarHolder: ProgressBar
    private var index: Int? = null
    private var inCache: Boolean = false
    private var cachedPostIsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val internetConnection = this.isInternetAvailable()

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        titleHolder = findViewById(R.id.textView)
        imageHolder = findViewById<ImageView>(R.id.imageView)
        progBarHolder = findViewById<ProgressBar>(R.id.progressBar2)
        val heartHolder = findViewById<ImageView>(R.id.heartHolder)

        val explBtn = findViewById<Button>(R.id.btnWeb)

        val number = intent.getIntExtra("id", 0)
        index = indexInList(number)
        if (internetConnection) {
            viewModel.getComicPost(number)
        } else {
            if (isInCache(number)) {
                viewModel.getComicPostCache(number)
                subscribeToComicPostCache()
            }
        }
        val heart = resources.getDrawable(R.drawable.ic_baseline_favorite_48)
        val emptyHeart = resources.getDrawable(R.drawable.ic_baseline_favorite_border_48)
        if (isInCache(number)) heartHolder.setImageDrawable(heart)

        viewModel.getResponse().observe(this, {

            if (it.isSuccessful) {
                titleHolder.text = it.body()?.title
                Glide.with(this)
                    .load(it.body()?.img)
                    .into(imageHolder)
                it.body()?.let { post ->
                    viewModel.createBitmap(post.img)
                    postFromInternet = post
                }
                progBarHolder.visibility = View.GONE
            }
        })

        subscibeToFinishedBitmap()

        heartHolder.setOnClickListener {
            if (cachedPostIsInitialized) {
                if (!isInCache(number)) {
                    GlobalList.globalList[index!!].isFavourite = true
                    viewModel.saveComicPostCache(cachedPost)
                    viewModel.saveComicListItem(comicListItem)
                    heartHolder.setImageDrawable(heart)
                    //showToast("has been saved to favorites!")
                } else {
                    GlobalList.globalList[index!!].isFavourite = false
                    heartHolder.setImageDrawable(emptyHeart)
                    viewModel.deleteComicPostCacheById(number)
                    viewModel.deleteComicListItemById(number)

                    //showToast("has been deleted from favorites!")
                }
            }
        }

        explBtn.setOnClickListener {
            if (internetConnection) {
                val intent = Intent(this, ExplanationActivity::class.java)
                intent.putExtra("id", number)
                intent.putExtra("title", comicListItem.title)
                this.startActivity(intent)
            } else {
                showToast("You cannot see explanations without internet-connection. Please check your connection!")
            }
        }
    }

    private fun indexInList(number: Int): Int {
        var placeInGlobalList = 0
        for (index in 0 until GlobalList.globalList.size) {
            if (GlobalList.globalList[index].id == number) {
                placeInGlobalList = index
                comicListItem = GlobalList.globalList[index]
            }
        }
        return placeInGlobalList
    }

    private fun isInCache(number: Int): Boolean {

        val comicListIt = GlobalList.globalList.find { number == it.id }
        val temp = comicListIt?.isFavourite == true
        comicListIt?.let {
            comicListItem = it
        }
        return temp
    }

    private fun subscribeToComicPostCache() {
        viewModel.comicPostCache.observe(this) {

            titleHolder.text = it.title
            Glide.with(this).load(it.imgBitMap).into(imageHolder)
            progBarHolder.visibility = View.GONE
        }
    }

    private fun subscibeToFinishedBitmap() {
        viewModel.getLiveBitMap().observe(this) {
            cachedPost = ComicPostCache(
                postFromInternet.month,
                postFromInternet.num,
                postFromInternet.link,
                postFromInternet.year,
                postFromInternet.news,
                postFromInternet.safe_title,
                postFromInternet.transcript,
                postFromInternet.alt,
                postFromInternet.img,
                postFromInternet.title,
                postFromInternet.day,
                it,
            )
            cachedPostIsInitialized = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this, message,
            Toast.LENGTH_SHORT
        ).show()
    }

    /* override fun onBackPressed() {
         val intent = Intent(this, MainActivity::class.java)
         startActivity(intent)
         super.onBackPressed()
     }*/

}