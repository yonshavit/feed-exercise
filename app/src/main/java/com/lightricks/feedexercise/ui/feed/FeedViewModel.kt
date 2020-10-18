package com.lightricks.feedexercise.ui.feed

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.lightricks.feedexercise.data.Factory
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.network.TemplatesMetadata
import com.lightricks.feedexercise.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalArgumentException

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel : ViewModel() {
    private val isLoading = MutableLiveData<Boolean>()
    private val isEmpty = MutableLiveData<Boolean>()
    private lateinit var feedRepository:FeedRepository
    private val feedItems = MediatorLiveData<List<FeedItem>>()
    private val networkErrorEvent = MutableLiveData<Event<String>>()

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = feedItems
    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    fun initRepo(context: Context){
        feedRepository = FeedRepository(Factory.createDatabase(context),Factory.createService())
        feedItems.addSource(feedRepository.getFeedItems(), Observer { t->
            isEmpty.value = t.isEmpty()
            feedItems.value = t
        })
        refresh()
    }

    @SuppressLint("CheckResult")
    fun refresh() {
        isLoading.value = true
        feedRepository.refresh().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                isLoading.value = false
            }, { error ->
                networkErrorEvent.value = Event(error.toString())
            })
    }
}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel() as T
    }
}