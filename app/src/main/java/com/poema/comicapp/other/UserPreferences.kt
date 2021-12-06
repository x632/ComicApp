package com.poema.comicapp.other

interface UserPreferences {
    fun saveOldAmount(oldAmount: Int)
    fun getOldAmount(): Int
}