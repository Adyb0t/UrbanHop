package com.example.urbanhop.data.location

import com.google.gson.annotations.SerializedName

data class QueryResponse(
    val results: List<VenueInfo>,
    val status: String
)

data class VenueInfo(
    @SerializedName("formatted_address")
    val formattedAddress: String,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)