package com.poema.comicapp.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: Repository)
    : ViewModel() {

    private val theResponse: MutableLiveData<Response<ComicPost>> = MutableLiveData()


    fun getComicPost(postNumber: Int) {
        viewModelScope.launch {
            val response = repository.getComicPost(postNumber)
            theResponse.value = response
        }
    }

    fun getResponse():MutableLiveData<Response<ComicPost>>{
        return theResponse
    }
}