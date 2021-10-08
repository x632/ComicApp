package com.poema.comicapp.ui.viewModels

import androidx.lifecycle.*
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.IsRead
import com.poema.comicapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.Response
import javax.inject.Inject


@HiltViewModel
class MainViewModel
@Inject
constructor(private val repository: Repository) : ViewModel() {

    private val _offlineComicList: MutableLiveData<List<ComicListItem>> = MutableLiveData()
    val offlineComicList: LiveData<List<ComicListItem>> = _offlineComicList

    private val _onlineComicList = MutableLiveData<List<ComicListItem>>()
    val onlineComicList :LiveData<List<ComicListItem>> = _onlineComicList

    val isReadList: LiveData<List<IsRead>> = repository.observeAllIsRead().asLiveData()

    fun getArchive() {

        CoroutineScope(IO).launch {
            val list: List<ComicListItem>? = repository.getArchive()
            if (list == null) {
                println("!!! Could not reach server!")
            }
            else {
                withContext(Main) {
                    _onlineComicList.value = list!!
                }
            }
        }
        viewModelScope.launch {
            val cachedList = repository.getFavorites()
            _offlineComicList.value = cachedList
        }
    }
}

