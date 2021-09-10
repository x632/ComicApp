package com.poema.comicapp.other

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

}