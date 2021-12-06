package com.poema.comicapp.other

import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferencesImpl @Inject constructor(
    private val prefs: SharedPreferences
):UserPreferences{

    override fun saveOldAmount(size: Int) {
        val editorShared = prefs.edit()
        editorShared.putInt("oldAmount", size)
        editorShared.apply()
    }

    override fun getOldAmount(): Int {
        return prefs.getInt("oldAmount", 0)
    }


}