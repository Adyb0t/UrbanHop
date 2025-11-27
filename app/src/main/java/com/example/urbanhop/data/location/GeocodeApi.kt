package com.example.urbanhop.data.location

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApi {
    @GET("json?region=my")
    suspend fun getVenueInfo(
        @Query("address") address: String
    ): Response<QueryResponse>
}