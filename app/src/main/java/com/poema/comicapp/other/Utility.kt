package com.poema.comicapp.other

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

object Utility {
    fun Context.isInternetAvailable(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected

    }
}