package com.example.urbanhop.data.location

import com.google.gson.annotations.SerializedName

data class LocationQueryResponse(
    val results: List<VenueInfo>,
    val status: String
)

data class VenueInfo(
    @SerializedName("formatted_address")
    val formattedAddress: String,
    val geometry: Geometry
)

data class Geometry(
    val location: LocationCoordinate
)

data class LocationCoordinate(
    val lat: Double,
    val lng: Double
)