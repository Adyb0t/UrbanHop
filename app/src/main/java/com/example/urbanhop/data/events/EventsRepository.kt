package com.example.urbanhop.data.events

import android.content.Context
import android.util.Log
import com.example.urbanhop.R
import com.example.urbanhop.data.location.GeocodeApi
import com.example.urbanhop.data.location.Location
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.gson.GsonBuilder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import org.simmetrics.metrics.CosineSimilarity
import java.io.InputStream
import org.simmetrics.builders.StringMetricBuilder.with
import org.simmetrics.simplifiers.Simplifiers
import org.simmetrics.tokenizers.Tokenizers

private val eventCollectionRef = Firebase.firestore.collection("events")
private const val isNotWeeklyUpdate = true
private const val TAG = "EventsRepo"

class EventsRepository(
    val context: Context,
    val geocodeApi: GeocodeApi
) {
    internal val gson = GsonBuilder().create()

    suspend fun loadEvents(
        code: String,
        codeQueryMap: Map<String, String>,
    ): List<Event> {

        val capturedEvents = mutableListOf<Event>()
        var filteredEvents = emptyList<Event>()

        if (isNotWeeklyUpdate) {
            try {
                capturedEvents +=
                    eventCollectionRef
                        .whereEqualTo("code", code)
                        .get()
                        .await()
                        .documents.mapNotNull { it.toObject<Event>() }.toMutableList()
                filteredEvents = capturedEvents
            } catch (e: Exception) {
                Log.e(TAG, "Error loading events: ${e.message}")
            }
        } else {
            val batch = Firebase.firestore.batch()
            eventCollectionRef.get().await().documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
            codeQueryMap.forEach { map ->
                //API query simulation
                val eventPerCode = context.resources.openRawResource(
                    when (map.key) {
                        "MBB" -> {
                            Log.i(TAG, "querying MBB: events near ${map.value}")
                            R.raw.events_bukit_bintang
                        }

                        "LKC" -> {
                            Log.i(TAG, "querying LKC: events near ${map.value}")
                            R.raw.events_klcc
                        }

                        "LKS" -> {
                            Log.i(TAG, "querying LKS: events near ${map.value}")
                            R.raw.events_kl_sentral
                        }

                        "LPS" -> {
                            Log.i(TAG, "querying LPS: events near ${map.value}")
                            R.raw.events_pasar_seni
                        }

                        else -> throw Exception("Unknown code")
                    }
                ).use { inputStream ->
                    readEventInfo(inputStream)
                }
                eventPerCode.forEach { it.code = map.key }
                capturedEvents.addAll(eventPerCode.distinctBy { it.title })
            }
            coroutineScope {
                capturedEvents.forEach { event ->
                    if (event.location == null) {
                        findCoordinateAndSaveEvent(event)
                    } else {
                        eventCollectionRef.add(event)
                    }
                }
            }
            filteredEvents = capturedEvents.distinct()
        }
        filteredEvents.forEach {
            Log.i(
                TAG,
                "Event: ${it.title} | ${it.date} | ${it.address} | ${it.location?.lat}, ${it.location?.lng}"
            )
        }
        return filteredEvents.filter { it.code == code }
    }
}

private fun EventsRepository.readEventInfo(inputStream: InputStream): List<Event> {
    val eventQueryResponseList = gson.fromJson(inputStream.reader(), EventQueryResponse::class.java)
    when (eventQueryResponseList.searchInfo.state) {

        "Fully empty" -> {
            return emptyList()
        }

        "Results for exact spelling" -> {
            return eventQueryResponseList.events.map { eventInfo ->
                with(eventInfo) {
                    Event(
                        title = title ?: "No title available",
                        date = date?.detailedDate ?: "No date available",
                        address = address ?: listOf("No address available", null),
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
                            name = venue?.name,
                            rating = venue?.rating ?: 0.0
                        )
                    )
                }
            }
        }

        else -> {
            throw Exception("Invalid search information")
        }
    }
}

private suspend fun EventsRepository.findCoordinateAndSaveEvent(event: Event) =
    try {
        if (event.address?.get(1) != null) {
            val location = searchCoordinate(
                with(event) {
                    address?.get(0) + ", " + address?.get(1)
                }
                    .replace(" ", "+")
                    .replace(",", "%2C")
                    .replace("&", "%26")
                    .replace("#", "%23")
            )
            location?.let {
                event.location = Coordinate(location.lat, location.lng)
            }
        }
        eventCollectionRef.add(event)
    } catch (e: Exception) {
        Log.e(TAG, "Error saving event: ${e.message}")
    }

private suspend fun EventsRepository.searchCoordinate(address: String): Location? {
    return geocodeApi.getVenueInfo(address).let { response ->
        when (response.code()) {
            200 -> {
                response.body().let { queryResponse ->
                    when (queryResponse?.status) {
                        "OK" -> {
                            if (queryResponse.results.size == 1) {
                                queryResponse.results.first().geometry.location
                            } else {
                                var mostSimilarIndex = 0
                                var highestScore = 0.0F
                                val metric =
                                    with(CosineSimilarity())
                                        .simplify(Simplifiers.toLowerCase())
                                        .tokenize(Tokenizers.whitespace())
                                        .build()
                                for (i in 0 until queryResponse.results.size) {
                                    val score = metric.compare(
                                        address,
                                        queryResponse.results[i].formattedAddress
                                    )
                                    if (score > highestScore) {
                                        highestScore = score
                                        mostSimilarIndex = i
                                    }
                                }
                                queryResponse.results[mostSimilarIndex].geometry.location
                            }
                        }

                        "ZERO_RESULTS" -> null
                        "OVER_DAILY_LIMIT" -> throw Exception("Exceeded daily limit")
                        "OVER_QUERY_LIMIT" -> throw Exception("Exceeded query limit")
                        "REQUEST_DENIED" -> throw Exception("Request denied")
                        "INVALID_REQUEST" -> throw Exception("Invalid request")
                        else -> throw Exception("Unknown error")
                    }
                }
            }

            else -> throw Exception("Error getting coordinate: ${response.code()}")
        }
    }
}



