package com.poema.comicapp.ui.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.poema.comicapp.data_sources.model.*
import com.poema.comicapp.data_sources.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import com.poema.comicapp.data_sources.model.GlobalList.globalList

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _response: MutableLiveData<Response<ComicPost>> = MutableLiveData()
    val response : LiveData<Response<ComicPost>> = _response

    val comicPostCache: MutableLiveData<ComicPostCache> = MutableLiveData()
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap:LiveData<Bitmap> = _bitmap

    val isReadList: LiveData<List<IsRead>> = repository.observeAllIsRead().asLiveData()

    var number = 0
    var cachedPost: ComicPostCache? = null
    var comicListItem: ComicListItem? = null
    var postFromInternet: ComicPost? = null
    var index: Int? = null



    fun getComicPost(postNumber: Int) {
        viewModelScope.launch {
            val response = repository.getComicPost(postNumber)
            _response.value = response
        }
    }

    fun saveComicPostCache(comicPostCache: ComicPostCache) {
        viewModelScope.launch {
            repository.saveComicPostCache(comicPostCache)
        }
    }

    fun saveComicListItem(comicListItem: ComicListItem) {
        viewModelScope.launch {
            repository.saveFavorite(comicListItem)
        }
    }

    fun getComicPostCache(id: Int) {
        viewModelScope.launch {
            val post = repository.findComicPostCacheById(id)
            comicPostCache.value = post
        }
    }

    fun deleteComicPostCacheById(number: Int) {
        viewModelScope.launch {
            repository.deleteComicPostCacheById(number)
        }
    }

    fun deleteComicListItemById(number: Int) {
        viewModelScope.launch {
            repository.deleteFavoriteById(number)
        }
    }

    fun createBitmap(url: String) {
        CoroutineScope(IO).launch{
            val stream = repository.getBitMap(url)
            withContext(Main){
                _bitmap.value = stream
            }
        }

    }

    fun indexInList(number: Int): Int {
        var placeInGlobalList = 0
        for (index in 0 until globalList.size) {
            if (globalList[index].id == number) {
                placeInGlobalList = index
                comicListItem = globalList[index]
            }
        }
       saveIsReadItem()
        return placeInGlobalList
    }

    private fun saveIsReadItem(){
        val isReadItem = comicListItem?.let { IsRead(it.id) }
        viewModelScope.launch{
            repository.saveIsRead(isReadItem!!)
        }
    }

    fun isInCache(number: Int): Boolean {
        val comicListIt = globalList.find { number == it.id }
        val temp = comicListIt?.isFavourite == true
        comicListIt?.let {
            comicListItem = it
        }
        return temp
    }
}

