package com.poema.comicapp.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.poema.comicapp.data_sources.model.GlobalList.globalList
import com.poema.comicapp.databinding.FragmentHomeBinding
import com.poema.comicapp.job_scheduler.NewComicsJobService
import com.poema.comicapp.other.Constants
import com.poema.comicapp.other.UserPreferences
import com.poema.comicapp.other.Utility.isInternetAvailable
import com.poema.comicapp.ui.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(

) {
@Inject lateinit var prefsClass : UserPreferences

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var comicAdapter: ComicListAdapter? = null
    private lateinit var tempSearchList: MutableList<ComicListItem>
    private lateinit var recycler: RecyclerView
    private lateinit var progBar: ProgressBar
    private var favButtonView : MenuItem? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        createNotificationChannel()
        createJobScheduler()
        setHasOptionsMenu(true)

        recycler = binding.recycler
        progBar = binding.progressBar
        if (requireContext().isInternetAvailable()) {
            progBar.visibility = View.VISIBLE
        }
        initializeRecycler()
        observeCache()
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
        scheduler.schedule(info)
    }

    private fun initializeRecycler() {
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            comicAdapter = ComicListAdapter(context)
            adapter = comicAdapter
            comicAdapter?.submitList(globalList)
        }
    }

    private fun observeCache() {
        viewModel.offlineComicList.observe(viewLifecycleOwner, {
            globalList = it as MutableList<ComicListItem>
            viewModel.cacheList = it
            viewModel.setFavorite()
            comicAdapter!!.submitList(globalList)
            if(viewModel.showFavorites) {
                    comicAdapter!!.submitList(viewModel.favoritesList)
                }
            progBar.visibility = View.GONE
           if(requireContext().isInternetAvailable()){
                subscribeToScrapeData()
            }
        })
    }

    private fun subscribeToScrapeData() {
        viewModel.onlineComicList.observe(viewLifecycleOwner, {
            globalList = it as MutableList<ComicListItem>
            tempSearchList = it
            viewModel.setFavorite()
            checkForNewItems(it)
            setHasRanBefore()
            viewModel.setBitMap()
            prefsClass.saveOldAmount(globalList.size)
            progBar.visibility = View.GONE
            comicAdapter!!.submitList(globalList)
            if (viewModel.showFavorites) {
                comicAdapter!!.submitList(viewModel.favoritesList)
            }
        })
    }

    private fun setHasRanBefore(){
        val preferences = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)
        val editor = preferences?.edit()
        editor?.putBoolean("RanBefore", true)
        editor?.apply()
    }

    private fun checkForNewItems(list: MutableList<ComicListItem>) {
        //makes sure it does not put a "new-icon" on all 2500 comics the first time app installs
        //once they all have been loaded once, it will create icons for newly created ones.
        //
        val preferences = activity?.getPreferences(AppCompatActivity.MODE_PRIVATE)
        val ranBefore = preferences?.getBoolean("RanBefore", false)
        if (!ranBefore!!) {
            val editor = preferences.edit()
            editor!!.putBoolean("RanBefore", true)
            editor.apply()
        } else {
            val oldAmountOfPosts = prefsClass.getOldAmount()
            val amountOfNewPosts = list.size - oldAmountOfPosts
            if (amountOfNewPosts > 0) {
                for (index in 0 until amountOfNewPosts) {
                    globalList[index].isNew = true
                }
                comicAdapter?.submitList(globalList)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater!!.inflate(R.menu.menu, menu)
        favButtonView = menu.findItem(R.id.fav)

        if (viewModel.showFavorites) {
            comicAdapter!!.submitList(viewModel.favoritesList)
            favButtonView!!.setIcon(R.drawable.action_bar_heart)
        }
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
                        globalList.forEach { item ->
                            if (item.id == text.toInt()) {
                                tempSearchList.add(item)
                            }
                        }
                        comicAdapter?.submitList(tempSearchList)
                    }
                    else if (text.isNotEmpty()) {
                        globalList.forEach { item2 ->
                            if (item2.title.lowercase(Locale.getDefault())
                                    .contains(text)
                            ) {
                                tempSearchList.add(item2)
                            }
                        }
                        comicAdapter?.submitList(tempSearchList)
                    } else {
                        comicAdapter?.submitList(globalList)
                    }
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.fav -> {
                viewModel.showFavorites = !viewModel.showFavorites
                if (viewModel.showFavorites) {
                    item.setIcon(R.drawable.action_bar_heart)
                    comicAdapter?.submitList(viewModel.favoritesList)

                } else {
                    item.setIcon(R.drawable.grey_border_heart)
                    comicAdapter?.submitList(globalList)
                }
            }
            R.id.update -> {
                if(requireContext().isInternetAvailable()){
                    progBar.visibility = View.VISIBLE
                    viewModel.reloadData()
                    viewModel.showFavorites = false
                    favButtonView?.setIcon(R.drawable.grey_border_heart)
                }else showToast("Currently there is no internet connection. You cannot reload data. Please check your connection!")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showToast(message: String) {
        Toast.makeText(
            requireContext(), message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
        val temp = activity as AppCompatActivity
        temp.apply{
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowTitleEnabled(true)
            supportActionBar?.show()
        }

    }
}

