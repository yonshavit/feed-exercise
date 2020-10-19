package com.lightricks.feedexercise.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.FeedResponse
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepository(private val db: FeedDatabase, private val service: FeedApiService) {
    private val URL_PREFIX = "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"
    private val feedDao: FeedDao = db.feedDao()
    private val liveData: LiveData<List<FeedItem>> = Transformations.map(feedDao.getAllLiveData()) {
        it.toFeedItems()
    }

    fun getFeedItems(): LiveData<List<FeedItem>>{
        return this.liveData
    }

    /**
     * Returns a Completable of the refreshing process
     * Contains a network request, so it must not be subscribedOn the main thread.
     */
    fun refresh(): Completable {
        return service.getFeed().flatMapCompletable { feedResponse ->
            insertResponseToDatabase(feedResponse) }.subscribeOn(Schedulers.io())
    }

    private fun insertResponseToDatabase(feedResponse: FeedResponse): Completable {
        val feedItemsEntityList = feedResponse.templatesMetadata.map { it ->
            FeedItemEntity(
                it.id,
                 URL_PREFIX + it.templateThumbnailURI,
                it.isPremium
            )
        }
        return feedDao.insertAll(feedItemsEntityList)
    }

    private fun List<FeedItemEntity>.toFeedItems(): List<FeedItem> {
        return map {
            FeedItem(it.id, it.thumbnailUrl, it.isPremium)
        }
    }
}
