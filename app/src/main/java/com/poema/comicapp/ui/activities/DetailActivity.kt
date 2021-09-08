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
    lateinit var comicListItem : ComicListItem
    lateinit var viewModel :DetailViewModel
    lateinit var postFromInternet: ComicPost
    lateinit var titleHolder : TextView
    lateinit var imageHolder : ImageView
    lateinit var progBarHolder : ProgressBar

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

        if(internetConnection) {
            viewModel.getComicPost(number)
        }else {
            for(item in GlobalCacheList.globalCacheList){
                if(number==item.id){
                    viewModel.getComicPostCache(number)
                    subscribeToComicPostCache()
                }
            }

        }

        viewModel.getResponse().observe(this, {

            if (it.isSuccessful) {
                titleHolder.text = it.body()?.title
                Glide.with(this)
                    .load(it.body()?.img)
                    .into(imageHolder)
                it.body()?.let{ post ->
                   viewModel.createBitmap(post.img)
                    postFromInternet = post
                }
                progBarHolder.visibility = View.GONE
            }
        })

        SubscibeToFinishedBitmap()


        favButton.setOnClickListener {
            for (index in 0 until GlobalList.globalList.size) {
                if (GlobalList.globalList[index].id == strNum.toInt()) {
                    GlobalList.globalList[index].isFavourite = true
                    comicListItem = GlobalList.globalList[index]
                }
            }
            viewModel.saveComicPostCache(cachedPost)
            viewModel.saveComicListItem(comicListItem)
        }
        explBtn.setOnClickListener{
            val intent = Intent(this, ExplanationActivity::class.java)
            intent.putExtra("id", number)
            this.startActivity(intent)
        }

    }

    private fun subscribeToComicPostCache() {
        viewModel.comicPostCache.observe(this){

                titleHolder.text = it.title
                Glide.with(this).load(it.imgBitMap).into(imageHolder)
                progBarHolder.visibility = View.GONE
        }
    }

    private fun SubscibeToFinishedBitmap() {
       viewModel.getLiveBitMap().observe(this){
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
               it,)
       }
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this, message,
            Toast.LENGTH_LONG
        ).show()
    }




}