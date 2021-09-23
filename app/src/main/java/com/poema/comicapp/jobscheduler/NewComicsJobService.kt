package com.poema.comicapp.jobscheduler

import android.app.PendingIntent
import android.app.job.JobParameters
import kotlinx.coroutines.*
import android.app.job.JobService
import android.content.Intent
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.poema.comicapp.R
import com.poema.comicapp.other.Constants
import com.poema.comicapp.other.Constants.CHANNEL_ID
import com.poema.comicapp.other.Constants.NOTIFICATION_ID
import com.poema.comicapp.ui.activities.MainActivity
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class NewComicsJobService : JobService() {
    private var jobCancelled = false
    private var job1: CompletableJob? = null

    override fun onStartJob(params: JobParameters): Boolean {
        println("!!!Job started")
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters) {

        job1 = Job()
        CoroutineScope(Dispatchers.IO + job1!!).launch {

            var listOfTitles: MutableList<String>? = null

            val prefs = PreferenceManager.getDefaultSharedPreferences(this@NewComicsJobService)
            val oldAmountOfPosts = prefs.getInt("oldAmount", 0)

            val request = Request.Builder()
                .url(Constants.ARCHIVE_URL)
                .build()
            OkHttpClient().newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str = response.body?.string()

                str?.let {
                    listOfTitles = startOrderingScrape(it)
                    val newTitle = listOfTitles!![0]
                    if (listOfTitles!!.size > oldAmountOfPosts) {
                        createNotification(newTitle)
                    }
                }
            }
            job1?.cancel()
            jobFinished(params, false)
        }
    }

    private fun createNotification(newTitle: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("XKCD")
            .setContentText("A new XKCD-comic, \"$newTitle\" available!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onStopJob(params: JobParameters): Boolean {
        job1?.cancel()
        jobCancelled = true
        return true
    }

    //scrapingfunctions

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