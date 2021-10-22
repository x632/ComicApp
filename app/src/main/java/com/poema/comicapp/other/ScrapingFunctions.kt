package com.poema.comicapp.other

import com.poema.comicapp.data_sources.model.ComicListItem


object ScrapingFunctions {

    fun doScrape(htmlString: String): MutableList<ComicListItem> {

        val startAfterThis = "publication date)<br /><br /"
        val stopAfterThis = "<a href=\"/1/\" title=\"2006-1-1\">Barrel - Part 1</a><br/>"
        val resultString = extractArea(htmlString, startAfterThis, stopAfterThis)
        val list = extractAll(resultString)

        return list
    }
    
    fun extractArea(
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
        return indexBeforeString + lengthOfWhatToFind
    }


    fun extractAll(resultString: String): MutableList<ComicListItem> {
        val tempList = resultString.split(">").toTypedArray()
        val titleList = mutableListOf<String>()
        for (listItem in tempList) {
            if (listItem.contains("</a")) {
                val tit = listItem.dropLast(3)
                titleList.add(tit)
            }
        }
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
        val finalList = mutableListOf<ComicListItem>()
        for (index in 0 until titleList.size) {
            val listItem =
                ComicListItem(
                    titleList[index],
                    finalNumbers[index],
                    finalDates[index],
                    false,
                    false
                )
            finalList.add(listItem)
        }
        return finalList
    }

}