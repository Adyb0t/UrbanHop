package com.example.urbanhop.data.events

import com.google.android.gms.maps.model.LatLng


data class Event(
    val title: String? = null,
    val date: String? = null,
    val address: List<String?>? = null,
    val mapLocation: String? = null,
    val description: String? = null,
    val ticketInfo: List<TicketInfo?>? = null,
    val venue: Venue? = null,
    var code: String? = null,
    var location: Coordinate? = null
)

data class TicketInfo(
    val source: String? = null,
    val link: String? = null
)

data class Venue(
    val name: String? = null,
    val rating: Double? = null
)

data class Coordinate(
    val lat: Double? = null,
    val lng: Double? = null
)