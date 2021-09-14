package com.poema.comicapp.ui.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.model.ComicPostCache
import com.poema.comicapp.model.GlobalList
import com.poema.comicapp.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val theResponse: MutableLiveData<Response<ComicPost>> = MutableLiveData()
    val comicPostCache: MutableLiveData<ComicPostCache> = MutableLiveData()

    var number = 0
    var cachedPost: ComicPostCache? = null
    var comicListItem: ComicListItem? = null
    var postFromInternet: ComicPost? = null
    var index: Int? = null
    var cachedPostIsInitialized = false


    fun getComicPost(postNumber: Int) {
        viewModelScope.launch {
            val response = repository.getComicPost(postNumber)
            theResponse.value = response
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

    fun getResponse(): MutableLiveData<Response<ComicPost>> {
        return theResponse
    }

    fun createBitmap(url: String) {
        repository.getBitMap(url)
    }

    fun getLiveBitMap(): MutableLiveData<Bitmap> {
        return repository.bitmap
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancelJobs()
    }

    fun indexInList(number: Int): Int {
        var placeInGlobalList = 0
        for (index in 0 until GlobalList.globalList.size) {
            if (GlobalList.globalList[index].id == number) {
                placeInGlobalList = index
                comicListItem = GlobalList.globalList[index]
            }
        }
        return placeInGlobalList
    }

    fun isInCache(number: Int): Boolean {

        val comicListIt = GlobalList.globalList.find { number == it.id }
        val temp = comicListIt?.isFavourite == true
        comicListIt?.let {
            comicListItem = it
        }
        return temp
    }

}

