package com.poema.comicapp.ui.activities


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
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
import com.poema.comicapp.job_scheduler.NewComicsJobService
import com.poema.comicapp.model.IsRead
import com.poema.comicapp.other.Constants.CHANNEL_ID
import com.poema.comicapp.other.Constants.CHANNEL_NAME
import com.poema.comicapp.other.Constants.JOB_ID
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var isReadList: MutableList<IsRead>? = null
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
        createNotificationChannel()
        createJobScheduler()

        internetConnection = this.isInternetAvailable()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        recycler = findViewById<RecyclerView>(R.id.recycler)
        progBar = findViewById<ProgressBar>(R.id.progressBar)
        if (this.internetConnection){
            progBar.visibility = View.VISIBLE
        }
        viewModel.getArchive()

        subscribeToCache()
        observeIsRead()
        subscribeToScrapeData()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH).apply{
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createJobScheduler() {
        val componentName = ComponentName(this, NewComicsJobService::class.java)
        val info = JobInfo.Builder(JOB_ID, componentName)
            .setPersisted(true)
            .setPeriodic(120L * 60L * 1000L)
            .build()
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            println("!!!Job scheduled")
        }
    }

    private fun initializeRecycler(){
        recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            comicAdapter = ComicListAdapter(context)
            adapter = comicAdapter
            comicAdapter?.let{ it.submitList(globalList)}
        }
    }

    private fun subscribeToCache() {
        viewModel.offlineComicList.observe(this,{
            globalList = it as MutableList<ComicListItem>
            cacheList = it as MutableList<ComicListItem>
            initializeRecycler()
        })
    }


    private fun subscribeToScrapeData() {
        viewModel.onlineComicList.observe(this, {
            val prefs = getDefaultSharedPreferences(this)
            globalList = it as MutableList<ComicListItem>
            tempSearchList = it
            checkForNewItems(it,prefs)

            for(index in 0 until cacheList.size){
                if( cacheList[index].isFavourite){
                    for(item in globalList){
                        if (item.id== cacheList[index].id){
                            item.isFavourite=true
                        }
                    }
                }
            }

            for(item1 in isReadList!!){
                for (item in globalList) {
                    if(item.id == item1.id){
                        item.isRead = true
                    }
                }
            }

            initializeRecycler()
            val editorShared = prefs.edit()
            editorShared.putInt("oldAmount", globalList.size)
            editorShared.apply()
            val preferences = getPreferences(MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("RanBefore", true)
            editor.apply()
            progBar.visibility = View.GONE
        })
    }

    private fun observeIsRead() {
        viewModel.isReadList.observe(this) {
            isReadList= it as MutableList<IsRead>?
        }
    }

    private fun checkForNewItems(list:MutableList<ComicListItem>, prefs: SharedPreferences) {
        val preferences = getPreferences(MODE_PRIVATE)
        val ranBefore = preferences.getBoolean("RanBefore", false)
        if (!ranBefore) {
            val editor = preferences.edit()
            editor.putBoolean("RanBefore", true)
            editor.apply()
        } else {

            val oldAmountOfPosts = prefs.getInt("oldAmount", 0)
            val amountOfNewPosts = list.size - oldAmountOfPosts
            if (amountOfNewPosts > 0) {
                for (index in 0 until amountOfNewPosts) {
                    globalList[index].isNew = true
                }
            }
        }
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
            it.submitList(globalList)
        }
        val view = this.currentFocus
        view?.clearFocus()
    }
}
