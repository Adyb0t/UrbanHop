package com.example.urbanhop.data.events

import com.google.gson.annotations.SerializedName

data class EventQueryResponse(
    @SerializedName("search_information")
    val searchInfo: ResultState,
    @SerializedName("events_results")
    val events: List<EventUnfiltered>
)

data class ResultState(
    @SerializedName("events_results_state")
    val state: String
)

data class EventUnfiltered(
    val title: String?,
    val date: EventDate?,
    val address: List<String>?,
    val link: String?,
    @SerializedName("event_location_map")
    val locationOnMap: EventMapLocation?,
    val description: String?,
    @SerializedName("ticket_info")
    val ticketInfo: List<TicketInfoUnfiltered>?,
    val venue: VenueUnfiltered?
)

data class EventDate(
    @SerializedName("start_date")
    val date: String?,
    @SerializedName("when")
    val detailedDate: String?
)

data class EventMapLocation(
    val link: String?
)

data class TicketInfoUnfiltered(
    val source: String?,
    val link: String?,
    @SerializedName("link_type")
    val linkType: String?
)

data class VenueUnfiltered(
    val name: String?,
    val rating: Double?,
    val reviews: Int?,
    val link: String?
)