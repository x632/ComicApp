package com.poema.comicapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poema.comicapp.R
import com.poema.comicapp.adapters.ComicListAdapter
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.GlobalCacheList
import com.poema.comicapp.model.GlobalList
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var comicAdapter: ComicListAdapter
    private lateinit var tempSearchList: MutableList<ComicListItem>
    private lateinit var viewModel: MainViewModel
    private lateinit var recycler : RecyclerView
    private lateinit var progBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val internetConnection = this.isInternetAvailable()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        recycler = findViewById<RecyclerView>(R.id.recycler)
        progBar = findViewById<ProgressBar>(R.id.progressBar)

        viewModel.getArchive(internetConnection)

        subscribeToScrapeData()
        subscribeToCache()
    }

    private fun subscribeToCache() {
        viewModel.offlineComicList.observe(this,{
            GlobalList.globalList = it as MutableList<ComicListItem>
            GlobalCacheList.globalCacheList = it
            recycler.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                comicAdapter = ComicListAdapter(context)
                adapter = comicAdapter
            }
            comicAdapter.submitList(GlobalList.globalList)
            progBar.visibility = View.GONE
        })
    }

    private fun subscribeToScrapeData() {
        viewModel.toUiFromViewModel.observe(this, {
            GlobalList.globalList = it
            tempSearchList = it
            for(index in 0 until GlobalCacheList.globalCacheList.size){
                if( GlobalCacheList.globalCacheList[index].isFavourite){
                    for(item in GlobalList.globalList){
                        if (item.id==GlobalCacheList.globalCacheList[index].id){
                            item.isFavourite=true
                        }
                    }
                }
            }

            tempSearchList = it
            recycler.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                comicAdapter = ComicListAdapter(context)
                adapter = comicAdapter
            }
            comicAdapter.submitList(GlobalList.globalList)


            progBar.visibility = View.GONE
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu?.findItem(R.id.search)
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                tempSearchList = mutableListOf()
                val searchText = newText?.lowercase(Locale.getDefault())
                searchText?.let { numb ->
                    if (numb.isNotEmpty() && numb.isDigitsOnly()) {
                        GlobalList.globalList.forEach { item ->
                            if (item.id == numb.toInt()) {
                                tempSearchList.add(item)
                                comicAdapter.submitList(tempSearchList)
                            }
                        }
                    } else if (numb.isNotEmpty() && numb=="fav") {
                        GlobalList.globalList.forEach { item3 ->
                            if (item3.isFavourite) {
                                tempSearchList.add(item3)
                                comicAdapter.submitList(tempSearchList)
                            }
                        }

                    } else if (numb.isNotEmpty()) {
                        GlobalList.globalList.forEach { item2 ->
                            if (item2.title.lowercase(Locale.getDefault())
                                    .contains(numb)
                            ) {
                                tempSearchList.add(item2)
                                comicAdapter.submitList(tempSearchList)
                            }
                        }
                    } else {
                        comicAdapter.submitList(GlobalList.globalList)
                    }
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


}
