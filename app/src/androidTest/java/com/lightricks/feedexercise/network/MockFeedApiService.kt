package com.lightricks.feedexercise.network

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single


class MockFeedApiService(val context: Context) : FeedApiService {
    override fun getFeed(): Single<TemplatesMetadata> {
        val kotlinJsonAdapterFactory = KotlinJsonAdapterFactory()
        val moshi = Moshi.Builder()
            .add(kotlinJsonAdapterFactory)
            .build()
        val jsonAdapter: JsonAdapter<TemplatesMetadata> =
            moshi.adapter(TemplatesMetadata::class.java)
        return Single.just(jsonAdapter.fromJson(context.assets.open("get_feed_response.json").bufferedReader().use { it.readText() }))
    }
}