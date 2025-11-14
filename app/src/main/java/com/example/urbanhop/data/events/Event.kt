package com.example.urbanhop.data.events

data class Event(
    val title: String,
    val date: String,
    val address: List<String>,
    val mapLocation: String,
    val description: String,
    val ticketInfo: List<TicketInfo>,
    val venue: Venue
)

data class TicketInfo(
    val source: String,
    val link: String
)

data class Venue(
    val name: String,
    val rating: Double
)