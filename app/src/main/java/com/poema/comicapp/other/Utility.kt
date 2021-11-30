package com.poema.comicapp.other

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

object Utility {
    fun Context.isInternetAvailable(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected

    /*    if (netInfo != null && netInfo.isConnected) {
            return true
        } else {
            //showErrorToast("Internet not available. Restricted to favorite comics only. Please check your connection!")
            return false
        }*/
    }

    private fun Context.showErrorToast(message: String?) {

        Toast.makeText(
            applicationContext, message,
            Toast.LENGTH_SHORT
        ).show()

    }
}