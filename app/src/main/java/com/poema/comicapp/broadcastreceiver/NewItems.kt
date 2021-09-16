package com.poema.comicapp.broadcastreceiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.poema.comicapp.R
import com.poema.comicapp.other.Constants
import com.poema.comicapp.other.Constants.CHANNEL_ID
import com.poema.comicapp.ui.activities.MainActivity
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class NewItems : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        var listOfTitles: MutableList<String>? = null
        val preferences = getDefaultSharedPreferences(context)
        var oldAmountOfPosts = preferences.getInt("oldAmount", 0)

        CoroutineScope(Dispatchers.IO).launch {

            val request = Request.Builder()
                .url(Constants.ARCHIVE_URL)
                .build()
            OkHttpClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str = response.body!!.string()

                str.let {
                    listOfTitles = startOrderingScrape(it)
                }
                val newTitle = listOfTitles!![0]
                println("!!! Fr broadcastreceiver: new size is :${listOfTitles!!.size} old size is: $oldAmountOfPosts last title is ${listOfTitles!![0]}")
                if (listOfTitles!!.size > oldAmountOfPosts) {
                    if (context != null && intent != null) {
                        makePossibleNotification(context, intent, newTitle)
                    }
                }
            }
        }
    }

    private fun makePossibleNotification(context: Context, intent: Intent, newTitle:String) {

        val myIntent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, myIntent, 0)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("XKCD")
            .setContentText("A new XKCD-comic, \"$newTitle\" available!")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)

        notificationManager.notify(1, notification)
    }


    private fun extractEntireList(
        htmlString: String,
        startAfterThis: String,
        stopAfterThis: String
    ): String {
        val startingIndex = getIndex(htmlString, startAfterThis)
        val endingIndex = getIndex(htmlString, stopAfterThis)
        return htmlString.slice(startingIndex..endingIndex)
    }

    private fun getIndex(htmlString: String, whatToFind: String): Int {
        val indexBeforeString = htmlString.indexOf(whatToFind, 0)
        val lengthOfWhatToFind = whatToFind.length
        val actualStartingIndex = indexBeforeString + lengthOfWhatToFind
        return actualStartingIndex
    }

    private fun startOrderingScrape(htmlString: String): MutableList<String> {

        val startAfterThis = "publication date)<br /><br /"
        val stopAfterThis = "<a href=\"/1/\" title=\"2006-1-1\">Barrel - Part 1</a><br/>"
        val resultString =
            extractEntireList(htmlString, startAfterThis, stopAfterThis)
        val list = resultString.split(">").toTypedArray()
        val titList = mutableListOf<String>()
        for (listItem in list) {
            if (listItem.contains("</a")) {
                val tit = listItem.dropLast(3)
                titList.add(tit)
            }
        }
        return titList
    }

}





