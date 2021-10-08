package com.poema.comicapp.data_sources

import com.poema.comicapp.data_sources.remote.RemoteDataSource
import com.poema.comicapp.data_sources.local.LocalDataSource


import javax.inject.Inject

data class DataSources @Inject constructor (
    val local: LocalDataSource,
    val remote: RemoteDataSource
)