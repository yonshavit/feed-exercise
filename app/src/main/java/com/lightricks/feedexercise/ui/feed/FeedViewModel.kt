package com.lightricks.feedexercise.ui.feed

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import com.lightricks.feedexercise.data.Factory
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel : ViewModel() {
    private val isLoading = MutableLiveData<Boolean>()
    private val isEmpty = MutableLiveData<Boolean>()
    private lateinit var feedRepository: FeedRepository
    private val feedItems = MediatorLiveData<List<FeedItem>>()
    private val networkErrorEvent = MutableLiveData<Event<String>>()
    private val NETWORK_ERROR_MESSAGE = "Couldn't load items, unexpected network error."

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = feedItems
    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    fun initRepo(feedRepository: FeedRepository) {
        this.feedRepository = feedRepository
        feedItems.addSource(feedRepository.getFeedItems(), Observer { feedItems ->
            isEmpty.value = feedItems.isEmpty()
            this.feedItems.value = feedItems
        })
        refresh()
    }

    @SuppressLint("CheckResult")
    fun refresh() {
        isLoading.value = true
        feedRepository.refresh()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                networkErrorEvent.value = Event(error?.toString() ?: NETWORK_ERROR_MESSAGE)
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