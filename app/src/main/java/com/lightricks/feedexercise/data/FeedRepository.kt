package com.lightricks.feedexercise.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.TemplatesMetadata
import io.reactivex.Completable

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepository(db:FeedDatabase, private val service: FeedApiService) {
    private val URL_PREQUEL:String = "https://assets.swishvideoapp.com/Android/demo/catalog/thumbnails/"
    private val feedDao: FeedDao = db.feedDao()
    private val liveData: LiveData<List<FeedItem>> = Transformations.map(feedDao.getAllLiveData()) {
        it.toFeedItems()
    }

    fun getFeedItems(): LiveData<List<FeedItem>>{
        return this.liveData
    }

    fun refresh(): Completable {
        return service.getFeed().flatMapCompletable { feedResponse ->
            insertResponseToDatabase(feedResponse) }
    }

    private fun insertResponseToDatabase(feedResponse: TemplatesMetadata):Completable{
        val feedItemsEntityList = feedResponse.templatesMetadata.map { it ->
            FeedItemEntity(
                it.id,
                 URL_PREQUEL + it.templateThumbnailURI,
                it.isPremium
            )
        }
        return feedDao.insertAll(*feedItemsEntityList.toTypedArray())
    }

    private fun List<FeedItemEntity>.toFeedItems(): List<FeedItem> {
        return map {
            FeedItem(it.id, it.thumbnailUrl, it.isPremium)
        }
    }
}
