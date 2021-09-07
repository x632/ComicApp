package com.poema.comicapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.poema.comicapp.R
import com.poema.comicapp.ui.viewModels.DetailViewModel
import com.poema.comicapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        val tvTitle = findViewById<TextView>(R.id.textView)
        val imageHolder = findViewById<ImageView>(R.id.imageView)
        val spinnerHolder = findViewById<ProgressBar>(R.id.progressBar2)
        val post = intent.getIntExtra("pos",0)
        tvTitle.text = post.toString()
        viewModel.getComicPost(post)

        viewModel.getResponse().observe(this,{

            if (it.isSuccessful) {

                println("!!! Title : ${it.body()?.title}")
                println("!!! Transcript : ${it.body()?.transcript}")
                tvTitle.text= it.body()?.num.toString()
                Glide.with(this)
                    .load(it.body()?.img)
                    .into(imageHolder)
                        spinnerHolder.visibility= View.GONE
            }
        })
    }


}