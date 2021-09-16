package com.poema.comicapp.other

import androidx.lifecycle.MutableLiveData
import com.poema.comicapp.model.ComicListItem

object ScrapingFunctions {

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

    fun startOrderingScrape(htmlString: String) {

        val startAfterThis = "publication date)<br /><br /"
        val stopAfterThis = "<a href=\"/1/\" title=\"2006-1-1\">Barrel - Part 1</a><br/>"
        val resultString =
            extractEntireList(htmlString, startAfterThis, stopAfterThis)
        extractTitles(resultString)
        //val list = MutableLiveData(comicList)
        //return list
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
        //extractNumAndDates(resultString, titList)
    }

}