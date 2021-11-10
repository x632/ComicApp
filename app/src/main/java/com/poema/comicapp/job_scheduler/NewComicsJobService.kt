package com.poema.comicapp.job_scheduler

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
import com.poema.comicapp.other.ScrapingFunctions
import com.poema.comicapp.ui.activities.MainActivity
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class NewComicsJobService : JobService() {

    private var job1: CompletableJob? = null

    override fun onStartJob(params: JobParameters): Boolean {
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters) {

        job1 = Job()
        CoroutineScope(Dispatchers.IO + job1!!).launch {


            val prefs = PreferenceManager.getDefaultSharedPreferences(this@NewComicsJobService)
            val oldAmountOfPosts = prefs.getInt("oldAmount", 0)

            val request = Request.Builder()
                .url(Constants.ARCHIVE_URL)
                .build()
            try {
                OkHttpClient().newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val str = response.body?.string()
                    str?.let {
                        val list = ScrapingFunctions.doScrape(it)
                        if (list.size > oldAmountOfPosts) {
                            createNotification()
                        }
                    }
                }
            } catch (e: IOException) {
                println("!!! ${e.message}")
            }
        }
        job1?.cancel()
        jobFinished(params, false)
    }


    override fun onStopJob(params: JobParameters): Boolean {
        job1?.cancel()
        return true
    }

    private fun createNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("XKCD")
            .setContentText("New XKCD-comics available!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


}