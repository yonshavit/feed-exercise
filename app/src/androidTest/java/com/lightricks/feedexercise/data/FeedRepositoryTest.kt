package com.lightricks.feedexercise.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.MockFeedApiService
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SimpleEntityReadWriteTest2 {
    private lateinit var feedDao: FeedDao
    private lateinit var db: FeedDatabase
    private lateinit var service: MockFeedApiService
    private val JSON_ITEMCOUNT = 10

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

    @Before
    fun createService() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        service = MockFeedApiService(context)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun refreshSavesData() {
        val feedRepo: FeedRepository = FeedRepository(db, service)

        feedRepo.refresh().blockingAwait()

        val count = JSON_ITEMCOUNT
        val feedItemsFromDb = feedDao.getAllLiveData().blockingObserve()

        assertThat(count).isEqualTo(feedItemsFromDb!!.size)
    }

    @Test
    fun feedItemsReflectsInsertion() {
        val feedRepo: FeedRepository = FeedRepository(db, service)

        val feedItem: FeedItemEntity = FeedItemEntity("1", "", true)
        val count = 1
        feedDao.insertAll(listOf(feedItem)).blockingAwait()

        val feedItems = feedRepo.getFeedItems().blockingObserve()
        assertThat(feedItems!!.size).isEqualTo(count)
    }
}

fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            value = t
            latch.countDown()
            removeObserver(this)
        }
    }

    observeForever(observer)
    latch.await(5, TimeUnit.SECONDS)
    return value
}
