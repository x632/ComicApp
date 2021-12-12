package com.poema.comicapp.ui.viewModels


import androidx.lifecycle.*
import com.poema.comicapp.data_sources.model.ComicListItem
import com.poema.comicapp.data_sources.model.GlobalList
import com.poema.comicapp.data_sources.model.GlobalList.globalList
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

    var cacheList: MutableList<ComicListItem> = mutableListOf()
    var favoritesList: MutableList<ComicListItem> = mutableListOf()
    val offlineComicList: LiveData<List<ComicListItem>> = repository.observeCache().asLiveData()

    private val _onlineComicList = MutableLiveData<List<ComicListItem>>()
    val onlineComicList: LiveData<List<ComicListItem>> = _onlineComicList

    var showFavorites = false

    init {
        CoroutineScope(IO).launch {
            val list: List<ComicListItem>? = repository.getArchive()
            withContext(Main) {
                _onlineComicList.value = list!!
            }
        }
    }

    fun setFavorite() {
        favoritesList = mutableListOf()
        for (index in 0 until cacheList.size) {
            if (cacheList[index].isFavourite) {
                for (item in globalList) {
                    if (item.id == cacheList[index].id) {
                        item.isFavourite = true
                        favoritesList.add(item)
                    }
                }
            }
        }
    }

    fun setBitMap() {
        for (index in 0 until cacheList.size) {
            for (item in globalList) {
                if (item.id == cacheList[index].id) {
                    item.bitmap = cacheList[index].bitmap
                    item.alt = cacheList[index].alt
                }
            }

        }
    }

    fun reloadData() {
        CoroutineScope(IO).launch {
            val list: List<ComicListItem>? = repository.getArchive()
            withContext(Main) {
                _onlineComicList.value = list!!
                setBitMap()
                setFavorite()
            }
        }
    }
}

