package com.example.urbanhop.data.event_stations

import com.google.android.gms.maps.model.LatLng

data class EventStation(
    val name: String,
    val queryName: String,
    val address: String,
    val coordinates: LatLng,
    val code: String
)