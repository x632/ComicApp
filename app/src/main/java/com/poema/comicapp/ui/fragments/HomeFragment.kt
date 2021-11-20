package com.poema.comicapp.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.ActionMenuView
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poema.comicapp.R
import com.poema.comicapp.adapters.ComicListAdapter
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.data_sources.model.GlobalList
import com.poema.comicapp.data_sources.model.IsRead
import com.poema.comicapp.databinding.FragmentHomeBinding
import com.poema.comicapp.job_scheduler.NewComicsJobService
import com.poema.comicapp.other.Constants
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var comicAdapter: ComicListAdapter? = null
    private lateinit var tempSearchList: MutableList<ComicListItem>
    private lateinit var recycler: RecyclerView
    private lateinit var progBar: ProgressBar
    private var internetConnection = false
    var everyOther = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        createNotificationChannel()
        createJobScheduler()
        setHasOptionsMenu(true)

        internetConnection = requireContext().isInternetAvailable()
        recycler = binding.recycler
        progBar = binding.progressBar
        if (internetConnection) {
            progBar.visibility = View.VISIBLE
        }

        initializeRecycler()
        observeCache()
        observeIsRead()
        subscribeToScrapeData()

        return binding.root
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID, Constants.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
            }
            val manager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createJobScheduler() {
        val componentName = ComponentName(requireContext(), NewComicsJobService::class.java)
        val info = JobInfo.Builder(Constants.JOB_ID, componentName)
            .setPersisted(true)
            .setPeriodic(120L * 60L * 1000L)
            .build()
        val scheduler =
            activity?.getSystemService(AppCompatActivity.JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            println("!!! crated schedule")
        }
    }

    private fun initializeRecycler() {
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            comicAdapter = ComicListAdapter(context)
            adapter = comicAdapter
            comicAdapter?.submitList(GlobalList.globalList)
        }
    }

    private fun observeCache() {
        viewModel.offlineComicList.observe(viewLifecycleOwner, {
            GlobalList.globalList = it as MutableList<ComicListItem>
            viewModel.cacheList = it as MutableList<ComicListItem>
            comicAdapter?.submitList(GlobalList.globalList)
            //denna gör två observationer om man har varit för länge i annan fragment.
            if (internetConnection) {
                subscribeToScrapeData()
            }
        })

    }


    private fun subscribeToScrapeData() {
        viewModel.onlineComicList.observe(viewLifecycleOwner, {
            val prefs = context!!.getSharedPreferences("oldAmount", 0)
            //val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            GlobalList.globalList = it as MutableList<ComicListItem>
            tempSearchList = it
            checkForNewItems(it, prefs)
            viewModel.setFavorite()
            viewModel.setIsRead()
            val preferences = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)
            val editorShared = prefs.edit()
            editorShared.putInt("oldAmount", GlobalList.globalList.size)
            editorShared.apply()
            val editor = preferences?.edit()
            editor?.putBoolean("RanBefore", true)
            editor?.apply()
            progBar.visibility = View.GONE
            comicAdapter?.submitList(GlobalList.globalList)

        })
    }

    private fun observeIsRead() {
        viewModel.isReadList.observe(viewLifecycleOwner) {
            viewModel.isReadMutList = (it as MutableList<IsRead>?)!!
        }
    }

    private fun checkForNewItems(list: MutableList<ComicListItem>, prefs: SharedPreferences) {
        //makes sure it does not put a "new-icon" on all 2500 comics the first time app installs
        //once they all have been loaded once, it will create notifications for newly created ones.
        //there is also the less noticable read/unread - icon that shows which comics are unseen.
        val preferences = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)
        val ranBefore = preferences?.getBoolean("RanBefore", false)
        if (!ranBefore!!) {
            val editor = preferences.edit()
            editor!!.putBoolean("RanBefore", true)
            editor.apply()
        } else {
            val oldAmountOfPosts = prefs.getInt("oldAmount", 0)
            println("!!! homefragment/checkForNewItems old amount = $oldAmountOfPosts")
            val amountOfNewPosts = list.size - oldAmountOfPosts
            if (amountOfNewPosts > 0) {
                for (index in 0 until amountOfNewPosts) {
                    GlobalList.globalList[index].isNew = true
                }
                comicAdapter?.submitList(GlobalList.globalList)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater!!.inflate(R.menu.menu, menu)
        val menuItem = menu.findItem(R.id.search)
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
                        GlobalList.globalList.forEach { item ->
                            if (item.id == text.toInt()) {
                                tempSearchList.add(item)
                            }
                        }
                        comicAdapter?.let {
                            it.submitList(tempSearchList)
                        }
                    } else if (text.isNotEmpty() && text == "fav") {
                        GlobalList.globalList.forEach { item3 ->
                            if (item3.isFavourite) {
                                tempSearchList.add(item3)
                            }

                        }
                        comicAdapter?.let {
                            it.submitList(tempSearchList)
                        }

                    } else if (text.isNotEmpty()) {
                        GlobalList.globalList.forEach { item2 ->
                            if (item2.title.lowercase(Locale.getDefault())
                                    .contains(text)
                            ) {
                                tempSearchList.add(item2)
                            }
                        }
                        comicAdapter?.submitList(tempSearchList)
                    } else {
                        comicAdapter?.submitList(GlobalList.globalList)
                    }
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        val a = activity as AppCompatActivity
        a.supportActionBar?.show()
        everyOther = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val list = mutableListOf<ComicListItem>()
        everyOther = !everyOther
        when (item.itemId) {
            R.id.fav -> {

                if (everyOther) {

                    GlobalList.globalList.forEach { item ->
                        if (item.isFavourite) {
                            list.add(item)
                        }
                    }
                    comicAdapter?.let {
                        it.submitList(list)
                    }
                } else {
                    comicAdapter?.let {
                        it.submitList(GlobalList.globalList)
                    }
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }

}

