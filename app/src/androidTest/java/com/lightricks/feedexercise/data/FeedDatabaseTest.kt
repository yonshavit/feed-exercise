package com.lightricks.feedexercise.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FeedDatabaseTest {
    private lateinit var feedDao: FeedDao
    private lateinit var db: FeedDatabase

    @get:Rule
    val executorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, FeedDatabase::class.java
        ).build()
        feedDao = db.feedDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertItemAndCheckCount() {
        val feedItem: FeedItemEntity = FeedItemEntity("1", "", true)
        feedDao.insertAll(listOf(feedItem)).blockingAwait()
        val count = feedDao.getCount()

        Truth.assertThat(count).isEqualTo(1)
    }

    @Test
    fun insertItemAndDeleteAll() {
        val feedItem: FeedItemEntity = FeedItemEntity("1", "", true)
        feedDao.insertAll(listOf(feedItem)).blockingAwait()
        feedDao.deleteAll().blockingAwait()
        val count = feedDao.getCount()
        Truth.assertThat(count).isEqualTo(0)
    }

    @Test
    fun insertItemAndGetAllItems() {
        val feedItem: FeedItemEntity = FeedItemEntity("1", "", true)
        feedDao.insertAll(listOf(feedItem)).blockingAwait()
        val getAllResponse = feedDao.getAllLiveData().blockingObserve()

        Truth.assertThat(getAllResponse!!).isEqualTo(listOf(feedItem))
    }
}
