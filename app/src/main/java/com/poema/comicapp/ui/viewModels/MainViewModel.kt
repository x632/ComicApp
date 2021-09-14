package com.poema.comicapp.ui.viewModels

import androidx.lifecycle.*
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.other.ScrapingFunctions
import com.poema.comicapp.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response

import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(private val repository: Repository) : ViewModel() {

    val offlineComicList: MutableLiveData<List<ComicListItem>> = MutableLiveData()
    private val comicList: MutableList<ComicListItem> = mutableListOf()

    //ordering webscrape in viewmodel first, then sending to UI
    private var stringFromRepo = repository.getLiveString()
    var toUiFromViewModel: LiveData<MutableList<ComicListItem>> =
        Transformations.switchMap(stringFromRepo) {
            startOrderingScrape(it)
        }


    fun getArchive(internetConnection: Boolean) {
        if (internetConnection) {
            repository.getArchiveAsString()
            viewModelScope.launch {
                val cachedList = repository.getFavorites()
                offlineComicList.value = cachedList
            }
        } else {
            getOnlyCachedList()
            }
    }

    fun getOnlyCachedList(){
        viewModelScope.launch {
            val cachedList = repository.getFavorites()
            offlineComicList.value = cachedList
        }
    }


    fun startOrderingScrape(htmlString: String): MutableLiveData<MutableList<ComicListItem>> {

        val startAfterThis = "publication date)<br /><br /"
        val stopAfterThis = "<a href=\"/1/\" title=\"2006-1-1\">Barrel - Part 1</a><br/>"
        val resultString =
            ScrapingFunctions.extractEntireList(htmlString, startAfterThis, stopAfterThis)
        extractTitles(resultString)
        val list = MutableLiveData(comicList)
        return list
    }


    fun extractTitles(resultString: String) {
        val list = resultString.split(">").toTypedArray()
        val titList = mutableListOf<String>()
        for (listItem in list) {
            if (listItem.contains("</a")) {
                val tit = listItem.dropLast(3)
                titList.add(tit)
            }
        }
        extractNumAndDates(resultString, titList)
    }

    private fun extractNumAndDates(resultString: String, finalTitles: MutableList<String>) {

        val list = resultString.split(">").toTypedArray()
        val numList = mutableListOf<String>()
        var counter = 0
        for (listItem in list) {
            if (listItem.contains("<a href=")) {
                counter++
                val tit2: String = if (counter == 1) {
                    listItem.drop(13)
                } else {
                    listItem.drop(14)
                }
                numList.add(tit2)
            }
        }
        var str1 = ""
        for (st in numList) {
            str1 += st
        }

        val parts = str1.split("\"")
        val finalNumbers = mutableListOf<Int>()
        val finalDates = mutableListOf<String>()
        for (part in parts) {
            if (part.contains("/")) {
                var createdPart = ""
                var finalCreatedPart = 0
                if (part == "/403/") {
                    createdPart = part.drop(1)
                    finalCreatedPart = (createdPart.dropLast(1)).toInt()
                    finalNumbers.add(finalCreatedPart)
                } else {
                    finalCreatedPart = (part.dropLast(1)).toInt()
                    finalNumbers.add(finalCreatedPart)
                }
            }
            if (part.contains("-")) {
                finalDates.add(part)
            }
        }
// add scraped material to comicList, then go back and return it to LiveData
        for (index in 0 until finalTitles.size) {
            val listItem =
                ComicListItem(finalTitles[index], finalNumbers[index], finalDates[index], false)

            comicList.add(listItem)

        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancelJobs()
    }
}
