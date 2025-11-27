package com.example.urbanhop.data.stations

import com.google.android.gms.maps.model.LatLng

data class Station(
    val name: String,
    val queryName: String,
    val address: String,
    val coordinates: LatLng,
    val code: String
)