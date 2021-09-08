package com.poema.comicapp.ui.viewModels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.model.ComicPostCache
import com.poema.comicapp.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val theResponse: MutableLiveData<Response<ComicPost>> = MutableLiveData()
    val comicPostCache: MutableLiveData<ComicPostCache> = MutableLiveData()

    fun getComicPost(postNumber: Int) {
        viewModelScope.launch {
            val response = repository.getComicPost(postNumber)
            theResponse.value = response
        }
    }

    fun saveComicPostCache(comicPostCache: ComicPostCache) {
        viewModelScope.launch {
            val id = repository.saveComicPostCache(comicPostCache)
            println("!!! id of saved comicPost is: $id")
        }
    }

    fun saveComicListItem(comicListItem: ComicListItem) {
        viewModelScope.launch {
            val id = repository.saveComicListItem(comicListItem)
            println("!!! Id of saved ComicListItem is $id")
        }
    }

    fun getComicPostCache(id: Int) {

        viewModelScope.launch {
            val post = repository.findComicPostById(id)
            comicPostCache.value = post
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

}

