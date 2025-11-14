package com.example.urbanhop.data.events

import android.content.Context
import com.example.urbanhop.R
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.InputStream

class EventsRepository(val context: Context) {

    private val gson = GsonBuilder().create()
    private val _events = MutableStateFlow(emptyList<Event>())
    val events: StateFlow<List<Event>> = _events.asStateFlow()
    private var loaded = false

    suspend fun loadEvents(): StateFlow<List<Event>> {
        if (!loaded) {
            loaded = true
            _events.value = withContext(Dispatchers.IO) {
                context.resources.openRawResource(R.raw.events_bukit_bintang).use { inputStream ->
                    readEventInfo(inputStream)
                }
            }
        }
        return events
    }

    private fun readEventInfo(inputStream: InputStream): List<Event> {
        val eventInfoList = gson.fromJson(inputStream.reader(), EventInfo::class.java)
        return eventInfoList.events.map { eventInfo ->
            with(eventInfo) {
                Event(
                    title = title ?: "No title available",
                    date = date?.detailedDate ?: "No date available",
                    address = address ?: listOf("No address available"),
                    mapLocation = locationOnMap?.link ?: "No map location available",
                    description = description ?: "No description available",
                    ticketInfo = ticketInfo?.map { ticket ->
                        TicketInfo(
                            source = ticket.source ?: "Unknown Source",
                            link = ticket.link ?: "No link available"
                        )
                    } ?: listOf(
                        TicketInfo(
                            source = "Unknown Source",
                            link = "No link available"
                        )
                    ),
                    venue = Venue(
                        name = venue?.name ?: "Unknown Venue",
                        rating = venue?.rating ?: 0.0
                    )
                )
            }
        }
    }
}

private data class EventInfo(
    @SerializedName("events_results")
    val events: List<EventUnfiltered>
)

private data class EventUnfiltered(
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

private data class EventDate(
    @SerializedName("start_date")
    val date: String?,
    @SerializedName("when")
    val detailedDate: String?
)

private data class EventMapLocation(
    val link: String?
)

private data class TicketInfoUnfiltered(
    val source: String?,
    val link: String?,
    @SerializedName("link_type")
    val linkType: String?
)

private data class VenueUnfiltered(
    val name: String?,
    val rating: Double?,
    val reviews: Int?,
    val link: String?
)



