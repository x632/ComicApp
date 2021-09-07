package com.poema.comicapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poema.comicapp.R
import com.poema.comicapp.adapters.ComicListAdapter
import com.poema.comicapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var comicAdapter: ComicListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val recycler = findViewById<RecyclerView>(R.id.recycler)
        viewModel.start()

        viewModel.toUiFromViewModel.observe(this, {
            val comicList = it
            recycler.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                comicAdapter = ComicListAdapter(context)
                adapter = comicAdapter
            }
            comicAdapter.submitList(comicList)
            comicAdapter.notifyDataSetChanged()
            val spinner = findViewById<ProgressBar>(R.id.progressBar)
            spinner.visibility = View.GONE
        })


    }



    //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    /*    //val scrapedStr = viewModel.getAllPosts()
        //viewModel.getAllPosts()
        viewModel.start()
        binding.buttonNext.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.increaseOrDecreasePostNumber(true)
          findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonBack.setOnClickListener {
            viewModel.increaseOrDecreasePostNumber(false)
            // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        //viewModel.getComicPost(5)
        viewModel.myResponse.observe(viewLifecycleOwner, Observer {
           if (it.isSuccessful) {

               println("!!! Title : ${it.body()?.title}")
               println("!!! Transcript : ${it.body()?.transcript}")
                binding.tvTitle.text= it.body()?.num.toString()
               Glide.with(this)
                   .load(it.body()?.img)
                   .into(binding.imageView)
               binding.progressBar.visibility= View.GONE
           }
        })*/


}
