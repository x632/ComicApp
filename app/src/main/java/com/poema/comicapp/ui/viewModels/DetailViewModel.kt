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

    private val _response: MutableLiveData<Response<ComicPostDto>> = MutableLiveData()
    val response : LiveData<Response<ComicPostDto>> = _response

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap:LiveData<Bitmap> = _bitmap

    var number = 0
    var comicListItem: ComicListItem? = null
    var postDtoFromInternet: ComicPostDto? = null
    var index: Int? = null



    fun getComicPost(postNumber: Int) {
        viewModelScope.launch {
            val response = repository.getComicPost(postNumber)
            _response.value = response
        }
    }


    fun saveComicListItem(comicListItem: ComicListItem) {
        viewModelScope.launch {
            repository.saveFavorite(comicListItem)
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
        return placeInGlobalList
    }



    fun isInCache(number: Int): Boolean {
        val comicListIt = globalList.find { number == it.id }
        val temp = comicListIt?.bitmap != null
        comicListIt?.let {
            comicListItem = it
        }
        return temp
    }
}

