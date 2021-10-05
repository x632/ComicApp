package com.poema.comicapp.data_sources.local

import javax.inject.Inject

data class LocalDataSource @Inject constructor(
    val comicDao: ComicDao
)