package com.poema.comicapp.datasources.local.db


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.poema.comicapp.data_sources.local.ComicDao
import com.poema.comicapp.data_sources.local.db.ComicDatabase
import com.poema.comicapp.data_sources.model.ComicListItem
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest

class ComicDaoTest {


    private lateinit var database: ComicDatabase
    private lateinit var comicDao: ComicDao
    private lateinit var testComicListItem : ComicListItem
    private lateinit var testComicListItem2 : ComicListItem


    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), ComicDatabase::class.java).allowMainThreadQueries()
          .build()
        comicDao = database.comicDao()

        testComicListItem = ComicListItem( "title", 3000, "theDate", null, null,
            isFavourite = false,
            isNew = true)
        testComicListItem2 = ComicListItem( "title", 3001, "theSecondDate", null, null,
            isFavourite = false,
            isNew = true)
    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun testIfInsertsComicListItemAndFindsItemById() = runBlocking {

        comicDao.insert(testComicListItem)
        val loadedItem = comicDao.findComicListItemById(3000)
        val result = loadedItem.id == testComicListItem.id
        assertThat(result).isTrue()
    }

    @Test
    fun testIfObservesTheFlow() = runBlocking {
        var result : List<ComicListItem> = listOf()
        val comicListItems = async {
            comicDao.observeComicListItems().take(1)
        }
        comicDao.insert(testComicListItem)
        comicDao.insert(testComicListItem2)
        comicListItems.await().collect {
            result = it

        }
        assertThat(result[1].date).isEqualTo("theDate")
        assertThat(result[0].date).isEqualTo("theSecondDate")
    }

}