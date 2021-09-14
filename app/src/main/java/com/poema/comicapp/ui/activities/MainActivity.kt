package com.poema.comicapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.poema.comicapp.model.GlobalList.globalList
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var comicAdapter: ComicListAdapter? = null
    private lateinit var tempSearchList: MutableList<ComicListItem>
    private lateinit var viewModel: MainViewModel
    private lateinit var recycler : RecyclerView
    private lateinit var progBar : ProgressBar
    private var internetConnection = false
    private lateinit var cacheList : MutableList<ComicListItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        internetConnection = this.isInternetAvailable()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        recycler = findViewById<RecyclerView>(R.id.recycler)
        progBar = findViewById<ProgressBar>(R.id.progressBar)

        viewModel.getArchive(internetConnection)

        subscribeToCache()
        subscribeToScrapeData()
    }

    private fun initializeRecycler(){
        recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            comicAdapter = ComicListAdapter(context)
            adapter = comicAdapter
        }
    }

    private fun subscribeToCache() {
        viewModel.offlineComicList.observe(this,{
            globalList = it as MutableList<ComicListItem>
            cacheList = it as MutableList<ComicListItem>
            initializeRecycler()
            comicAdapter?.let{ it.submitList(globalList)}
            progBar.visibility = View.GONE
        })
    }

    private fun subscribeToScrapeData() {
        viewModel.toUiFromViewModel.observe(this, {
            globalList = it
            tempSearchList = it
            for(index in 0 until cacheList.size){
                if( cacheList[index].isFavourite){
                    for(item in globalList){
                        if (item.id== cacheList[index].id){
                            item.isFavourite=true
                        }
                    }
                }
            }
            initializeRecycler()
            comicAdapter?.let{it.submitList(globalList)}
            progBar.visibility = View.GONE
            val preferences = getPreferences(MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("RanBefore", false)
            editor.apply()

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
                searchText?.let { text ->
                    if (text.isNotEmpty() && text.isDigitsOnly()) {
                            globalList.forEach { item ->
                            if (item.id == text.toInt()) {
                                tempSearchList.add(item)
                                comicAdapter?.let{it.submitList(tempSearchList)}
                            }
                        }
                    } else if (text.isNotEmpty() && text=="fav") {
                            globalList.forEach { item3 ->
                            if (item3.isFavourite) {
                                tempSearchList.add(item3)
                                comicAdapter?.let{it.submitList(tempSearchList)}
                            }
                        }

                    } else if (text.isNotEmpty()) {
                            globalList.forEach { item2 ->
                            if (item2.title.lowercase(Locale.getDefault())
                                    .contains(text)
                            ) {
                                tempSearchList.add(item2)
                                comicAdapter?.let{it.submitList(tempSearchList)}
                            }
                        }
                    } else {
                        comicAdapter?.let{it.submitList(globalList)}
                    }
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun onResume() {
        super.onResume()
        comicAdapter?.let{
            //scenario: if didn't have internetconnection at startup and regains connection while in detailscreen, then goes back: below makes sure all items are reloaded - only then.
           if(globalList.size < 2511 && this.isInternetAvailable()){
               val preferences = getPreferences(MODE_PRIVATE)
               val ranBefore = preferences.getBoolean("RanBefore", false)
               if (!ranBefore) {
                   val editor = preferences.edit()
                   editor.putBoolean("RanBefore", true)
                   editor.apply()
                   recreate()
               }
           }
            it.submitList(globalList)
        }
    }
}
