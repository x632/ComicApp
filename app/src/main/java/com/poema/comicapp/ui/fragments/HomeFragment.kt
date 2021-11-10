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
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
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
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var comicAdapter: ComicListAdapter? = null
    private lateinit var tempSearchList: MutableList<ComicListItem>
    private lateinit var viewModel: MainViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var progBar: ProgressBar
    private var internetConnection = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        createNotificationChannel()
        createJobScheduler()

        internetConnection = requireContext().isInternetAvailable()
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        recycler = binding.recycler
        progBar = binding.progressBar
        if (this.internetConnection) {
            progBar.visibility = View.VISIBLE
        }
        viewModel.getArchive()

        subscribeToCache()
        observeIsRead()
        subscribeToScrapeData()
        return view
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
            println("!!!Job scheduled")
        }
    }

    private fun initializeRecycler() {
        recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            comicAdapter = ComicListAdapter(context)
            adapter = comicAdapter
            comicAdapter?.submitList(GlobalList.globalList)
        }
    }

    private fun subscribeToCache() {
        viewModel.offlineComicList.observe(viewLifecycleOwner, {
            GlobalList.globalList = it as MutableList<ComicListItem>
            viewModel.cacheList = it as MutableList<ComicListItem>
            initializeRecycler()
        })
    }


    private fun subscribeToScrapeData() {
        viewModel.onlineComicList.observe(viewLifecycleOwner, {
            val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
            GlobalList.globalList = it as MutableList<ComicListItem>
            tempSearchList = it
            checkForNewItems(it, prefs)
            viewModel.setFavorite()
            viewModel.setIsRead()
            initializeRecycler()
            val preferences = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)
            val editorShared = prefs.edit()
            editorShared.putInt("oldAmount", GlobalList.globalList.size)
            editorShared.apply()
            val editor = preferences?.edit()
            editor?.putBoolean("RanBefore", true)
            editor?.apply()
            progBar.visibility = View.GONE
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
        //there is also the less noticable read/unread - icon that shows which ones are unseen.
        val preferences = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)
        val ranBefore = preferences?.getBoolean("RanBefore", false)
        if (ranBefore!!) {
            val editor = preferences.edit()
            editor!!.putBoolean("RanBefore", true)
            editor.apply()
        } else {
            val oldAmountOfPosts = prefs.getInt("oldAmount", 0)
            val amountOfNewPosts = list.size - oldAmountOfPosts
            if (amountOfNewPosts > 0) {
                for (index in 0 until amountOfNewPosts) {
                    GlobalList.globalList[index].isNew = true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu, menu)
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
                                comicAdapter?.let { it.submitList(tempSearchList) }
                            }
                        }
                    } else if (text.isNotEmpty() && text == "fav") {
                        GlobalList.globalList.forEach { item3 ->
                            if (item3.isFavourite) {
                                tempSearchList.add(item3)
                                comicAdapter?.let { it.submitList(tempSearchList) }
                            }
                        }

                    } else if (text.isNotEmpty()) {
                        GlobalList.globalList.forEach { item2 ->
                            if (item2.title.lowercase(Locale.getDefault())
                                    .contains(text)
                            ) {
                                tempSearchList.add(item2)
                                comicAdapter?.submitList(tempSearchList)
                            }
                        }
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
        comicAdapter?.submitList(GlobalList.globalList)
        val view = activity?.currentFocus
        view?.clearFocus()
    }
}

