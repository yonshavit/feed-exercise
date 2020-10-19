package com.lightricks.feedexercise.data

import android.content.Context
import androidx.room.Room
import com.lightricks.feedexercise.database.FeedDatabase
import com.lightricks.feedexercise.network.FeedApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class Factory {
    companion object Factory {
        fun createDatabase(context: Context): FeedDatabase {
            return (Room.inMemoryDatabaseBuilder(
                context, FeedDatabase::class.java
            ).build())
        }

        fun createService(): FeedApiService {
            val kotlinJsonAdapterFactory = KotlinJsonAdapterFactory()
            val moshi = Moshi.Builder()
                .add(kotlinJsonAdapterFactory)
                .build()
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl("https://assets.swishvideoapp.com/Android/demo/")
                .build()
            return retrofit.create(FeedApiService::class.java)
        }

        fun createFeedRepository(db: FeedDatabase, service: FeedApiService): FeedRepository{
            return FeedRepository(db,service)
        }
    }
}