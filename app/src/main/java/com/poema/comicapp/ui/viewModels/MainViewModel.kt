package com.poema.comicapp.ui.viewModels


import androidx.lifecycle.*
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.data_sources.model.GlobalList.globalList
import com.poema.comicapp.data_sources.model.IsRead
import com.poema.comicapp.data_sources.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject


@HiltViewModel
class MainViewModel
@Inject
constructor(private val repository: Repository) : ViewModel() {

    var isReadMutList = mutableListOf<IsRead>()
    var cacheList: MutableList<ComicListItem> = mutableListOf()

    val offlineComicList: LiveData<List<ComicListItem>> = repository.observeFavorites().asLiveData()

    private val _onlineComicList = MutableLiveData<List<ComicListItem>>()
    val onlineComicList: LiveData<List<ComicListItem>> = _onlineComicList

    val isReadList: LiveData<List<IsRead>> = repository.observeAllIsRead().asLiveData()

    init {
        println("!!! init has been run!!")
        CoroutineScope(IO).launch {
            val list: List<ComicListItem>? = repository.getArchive()
            if (list == null) {
                println("!!! Could not reach server!")
            } else {
                withContext(Main) {
                    _onlineComicList.value = list!!
                }
            }
        }
    }

    fun setFavorite() {
        for (index in 0 until cacheList.size) {
            if (cacheList[index].isFavourite) {
                for (item in globalList) {
                    if (item.id == cacheList[index].id) {
                        item.isFavourite = true
                    }
                }
            }
        }
    }

    fun setIsRead() {
        for (item1 in isReadMutList) {
            for (item in globalList) {
                if (item.id == item1.id) {
                    item.isRead = true
                }
            }
        }

    }

}

