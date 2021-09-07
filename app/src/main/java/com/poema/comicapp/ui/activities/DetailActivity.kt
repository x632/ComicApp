package com.poema.comicapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.poema.comicapp.R
import com.poema.comicapp.model.GlobalList
import com.poema.comicapp.ui.viewModels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        val tvTitle = findViewById<TextView>(R.id.textView)
        val imageHolder = findViewById<ImageView>(R.id.imageView)
        val favButton = findViewById<Button>(R.id.btnAddFav)
        val explBtn = findViewById<Button>(R.id.btnWeb)
        val spinnerHolder = findViewById<ProgressBar>(R.id.progressBar2)
        val number = intent.getIntExtra("id", 0)
        val strNum = number.toString()

        viewModel.getComicPost(number)

        viewModel.getResponse().observe(this, {

            if (it.isSuccessful) {
                tvTitle.text = it.body()?.title
                Glide.with(this)
                    .load(it.body()?.img)
                    .into(imageHolder)
                spinnerHolder.visibility = View.GONE
            }
        })

        favButton.setOnClickListener {
            for (index in 0 until GlobalList.globalList.size) {
                if (GlobalList.globalList[index].id == strNum) {
                    GlobalList.globalList[index].isFavourite = true
                }
            }

        }
        explBtn.setOnClickListener{
            val intent = Intent(this, ExplanationActivity::class.java)
            intent.putExtra("id", number)
            this.startActivity(intent)
        }

    }
}