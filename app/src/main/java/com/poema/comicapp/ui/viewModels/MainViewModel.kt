package com.poema.comicapp.ui.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.poema.comicapp.model.ComicListItem
import com.poema.comicapp.model.ComicPost
import com.poema.comicapp.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response

import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(private val repository: Repository) : ViewModel() {

    private val comicList : MutableList<ComicListItem> = mutableListOf()
    private val myResponse: MutableLiveData<Response<ComicPost>> = MutableLiveData()
    var post = 1

    //order webscrape in viewmodel first, then send to UI
    var stringFromRepo = repository.getLiveString()
    var toUiFromViewModel: LiveData<MutableList<ComicListItem>> =
        Transformations.switchMap(stringFromRepo) {
            startOrderScrape(it)
        }

    fun start() {
        repository.getArchiveAsString()
    }

    /*  fun increaseOrDecreasePostNumber(increase: Boolean = true) {
          if (increase && post < 2500) {
              post++
              getComicPost(post)
          } else if (post > 1) {
              post--
              getComicPost(post)
          }
      }*/

    fun getComicPost(postNumber: Int) {
        viewModelScope.launch {
            val response = repository.getComicPost(postNumber)
            myResponse.value = response
        }
    }

    /* fun getAllPosts() {
         var stillPostsLeft = true
         var failedOnce = false
         val theList = mutableListOf<ComicPost>()
         var counter = 0
         viewModelScope.launch {
             while (stillPostsLeft) {
                 counter++
                 val response = repository.getComicPost(counter)
                 if (response.isSuccessful) {
                     response.body()?.let {
                         theList.add(it)
                         println("!!! listnumber = ${it.num} title: ${it.title} url: ${it.img}")
                     }
                 } else {
                     if (!failedOnce) {
                         counter++
                         failedOnce = true
                         println("!!! FAILED ONCE!")
                     } else {
                         stillPostsLeft = false
                         println("!!! FAILED TWICE - End of data")
                     }
                 }
             }
         }
     }*/

    fun startOrderScrape(htmlString: String): MutableLiveData<MutableList<ComicListItem>> {

        val startAfterThis = "publication date)<br /><br /"
        val stopAfterThis = "<a href=\"/1/\" title=\"2006-1-1\">Barrel - Part 1</a><br/>"
        val resultString = extractEntireList(htmlString, startAfterThis, stopAfterThis)
        extractTitles(resultString)
        val list = MutableLiveData(comicList)
        return list
    }

    fun extractEntireList(
        htmlString: String,
        startAfterThis: String,
        stopAfterThis: String
    ): String {
        val startingIndex = getIndex(htmlString, startAfterThis)
        val endingIndex = getIndex(htmlString, stopAfterThis)
        return htmlString.slice(startingIndex..endingIndex)
    }

    fun getIndex(htmlString: String, whatToFind: String): Int {
        val indexBeforeString = htmlString.indexOf(whatToFind, 0)
        val lengthOfWhatToFind = whatToFind.length
        val actualStartingIndex = indexBeforeString + lengthOfWhatToFind
        return actualStartingIndex
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
        val finalNumbers = mutableListOf<String>()
        val finalDates = mutableListOf<String>()
        for (part in parts) {
            if (part.contains("/")) {
                finalNumbers.add(part.dropLast(1))
            }
            if (part.contains("-")) {
                finalDates.add(part)
            }
        }
        // add scraped material to comicList, then go back and return it to LiveData
        for (index in 0 until finalTitles.size){
            val listItem = ComicListItem(finalDates[index],finalNumbers[index],finalTitles[index],false)
            comicList.add(listItem)
        }
    }
}
