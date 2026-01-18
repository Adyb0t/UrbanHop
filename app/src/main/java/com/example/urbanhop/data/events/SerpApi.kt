package com.example.urbanhop.data.events

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SerpApi {
    @GET("search.json?engine=google_events&location=Malaysia&gl=my&hl=en")
    suspend fun getEventInfo(
        @Query("q") query: String,
        @Query("start") start: Int
    ): Response<EventQueryResponse>
}