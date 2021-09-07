package com.poema.comicapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poema.comicapp.R
import com.poema.comicapp.adapters.ComicListAdapter
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.GlobalList
import com.poema.comicapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var comicAdapter: ComicListAdapter
    private lateinit var tempSearchList : MutableList<ComicListItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val recycler = findViewById<RecyclerView>(R.id.recycler)
        viewModel.getArchive()

        viewModel.toUiFromViewModel.observe(this, {

            GlobalList.globalList = it
            tempSearchList = it
            recycler.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                comicAdapter = ComicListAdapter(context)
                adapter = comicAdapter
            }
            comicAdapter.submitList(GlobalList.globalList)

            val spinner = findViewById<ProgressBar>(R.id.progressBar)
            spinner.visibility = View.GONE
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val menuItem = menu?.findItem(R.id.search)
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println("!!! TEST!!!!!  ${newText?.lowercase(Locale.getDefault())}")
                tempSearchList = mutableListOf()
                val searchText = newText?.lowercase(Locale.getDefault())
                if(searchText!!.isNotEmpty()){
                    GlobalList.globalList.forEach {
                        println("TITLE: ${it.title}")
                        if(it.title.lowercase(Locale.getDefault()).contains(searchText!!)){
                            tempSearchList.add(it)
                            comicAdapter.submitList(tempSearchList)
                        }
                    }

                } else {
                        comicAdapter.submitList(GlobalList.globalList)
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
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
