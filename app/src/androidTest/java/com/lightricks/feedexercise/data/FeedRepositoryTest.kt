package com.lightricks.feedexercise.data

import android.content.Context
import android.util.Log
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
import com.lightricks.feedexercise.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
    fun createService(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        service = MockFeedApiService(context)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun refreshSavesDataAndFeedItemsContainsData(){
        val feedRepo: FeedRepository = FeedRepository(db,service)

        feedRepo.refresh().blockingAwait()
        val count = feedDao.getCount()

        val feedItems = feedRepo.getFeedItems().blockingObserve()
        val feedItemsFromDb = feedDao.getAllLiveData().blockingObserve()

        assertThat(count).isEqualTo(feedItemsFromDb!!.size)
        assertThat(count).isEqualTo(feedItems!!.size)

    }

    @Test
    fun insertItemAndCheckCount() {
        val feedItem: FeedItemEntity = FeedItemEntity("1", "", true)
        feedDao.insertAll(feedItem).blockingAwait()
        val count = feedDao.getCount()

        assertThat(count).isEqualTo(1)
    }

    @Test
    fun insertItemAndDeleteAll() {
        val feedItem: FeedItemEntity = FeedItemEntity("1", "", true)
        feedDao.insertAll(feedItem).blockingAwait()
        feedDao.deleteAll().blockingAwait()
        val count = feedDao.getCount()
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun insertItemAndGetAllItems() {
        val feedItem: FeedItemEntity = FeedItemEntity("1", "", true)
        feedDao.insertAll(feedItem).blockingAwait()
        val getAllResponse = feedDao.getAllLiveData().blockingObserve()

        assertThat(getAllResponse!!).isEqualTo(listOf(feedItem))
    }


}

private fun <T> LiveData<T>.blockingObserve(): T? {
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
