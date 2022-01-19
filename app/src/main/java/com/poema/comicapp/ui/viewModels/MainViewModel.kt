package com.poema.comicapp.ui.viewModels


import androidx.lifecycle.*
import com.poema.comicapp.data_sources.model.ComicListItem
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

    fun setBitMapAndFav() {

            favoritesList = mutableListOf()
            for (index in 0 until cacheList.size) {
                for (item in globalList) {
                    if (item.id == cacheList[index].id) {
                        item.bitmap = cacheList[index].bitmap
                        item.alt = cacheList[index].alt
                        item.isFavourite = cacheList[index].isFavourite
                        if (cacheList[index].isFavourite) {
                            favoritesList.add(item)
                        }
                    }
                }

            }

      
    }

   /* fun setBitMapAndFav() {
            favoritesList = mutableListOf()
            var position = 0
            for (index in 0 until cacheList.size) {
                position = if (cacheList[index].id < 404) {
                    (globalList.size) - cacheList[index].id
                } else {
                    (globalList.size + 1) - cacheList[index].id
                }
                globalList[position].bitmap = cacheList[index].bitmap
                globalList[position].alt = cacheList[index].alt
                globalList[position].isFavourite = cacheList[index].isFavourite
                if (cacheList[index].isFavourite) {
                    val item = globalList[position]
                    favoritesList.add(item)
                }
            }
    }*/


    fun reloadData() {
        CoroutineScope(IO).launch {
            val list: List<ComicListItem>? = repository.getArchive()
            withContext(Main) {
                _onlineComicList.value = list!!
                setBitMapAndFav()
            }
        }
    }
}

