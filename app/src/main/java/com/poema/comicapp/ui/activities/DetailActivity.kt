package com.poema.comicapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.poema.comicapp.R
import com.poema.comicapp.model.*
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    lateinit var cachedPost: ComicPostCache
    lateinit var comicListItem: ComicListItem
    lateinit var viewModel: DetailViewModel
    lateinit var postFromInternet: ComicPost
    lateinit var titleHolder: TextView
    lateinit var imageHolder: ImageView
    lateinit var progBarHolder: ProgressBar
    private var inCache: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val internetConnection = this.isInternetAvailable()

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        titleHolder = findViewById<TextView>(R.id.textView)
        imageHolder = findViewById<ImageView>(R.id.imageView)
        progBarHolder = findViewById<ProgressBar>(R.id.progressBar2)

        val favButton = findViewById<Button>(R.id.btnAddFav)
        val explBtn = findViewById<Button>(R.id.btnWeb)

        val number = intent.getIntExtra("id", 0)
        val strNum = number.toString()

        if (internetConnection) {
            inCache = isInCache(number)
            viewModel.getComicPost(number)
        } else {
            inCache = isInCache(number)
            if (inCache) {
                viewModel.getComicPostCache(number)
                subscribeToComicPostCache()
            }
        }
        if (inCache) favButton.text = "remove from cache"

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

        SubscibeToFinishedBitmap()

        favButton.setOnClickListener {

            if (!inCache) {
                for (index in 0 until GlobalList.globalList.size) {
                    if (GlobalList.globalList[index].id == number) {
                        GlobalList.globalList[index].isFavourite = true
                        comicListItem = GlobalList.globalList[index]

                    }
                }
                favButton.text = "remove from favs"
                viewModel.saveComicPostCache(cachedPost)
                viewModel.saveComicListItem(comicListItem)

            }   else{
                for (index in 0 until GlobalList.globalList.size) {
                    if (GlobalList.globalList[index].id == number) {
                        GlobalList.globalList[index].isFavourite = false
                        comicListItem = GlobalList.globalList[index]
                    }
                }
                favButton.text = "add to favorites"
                viewModel.deleteComicPostCacheById(number)
                viewModel.deleteComicListItemById(number)
            }
        }

        explBtn.setOnClickListener {
            val intent = Intent(this, ExplanationActivity::class.java)
            intent.putExtra("id", number)
            this.startActivity(intent)
        }

    }

    private fun isInCache(number: Int):Boolean {
        var temp = false
       val comicListItem = GlobalList.globalList.find{number == it.id}
        temp = comicListItem?.isFavourite == true
        return temp
    }

    private fun subscribeToComicPostCache() {
        viewModel.comicPostCache.observe(this) {

            titleHolder.text = it.title
            Glide.with(this).load(it.imgBitMap).into(imageHolder)
            progBarHolder.visibility = View.GONE
        }
    }

    private fun SubscibeToFinishedBitmap() {
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
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this, message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

}