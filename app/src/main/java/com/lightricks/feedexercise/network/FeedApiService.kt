package com.lightricks.feedexercise.network

import io.reactivex.Single
import retrofit2.http.GET


/**
 * todo: add the FeedApiService interface and the Retrofit and Moshi code here
 */

interface FeedApiService {
    @GET("feed.json")
    fun getFeed(): Single<TemplatesMetadata>;
}