package com.example.urbanhop.data.events

import android.util.Log
import android.location.Location
import com.example.urbanhop.data.location.GeocodeApi
import com.example.urbanhop.data.location.LocationCoordinate
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.simmetrics.builders.StringMetricBuilder.with
import org.simmetrics.metrics.CosineSimilarity
import org.simmetrics.simplifiers.Simplifiers
import org.simmetrics.tokenizers.Tokenizers
import kotlin.math.roundToInt

private val eventCollectionRef = Firebase.firestore.collection("events")
private val metadataUpdateRef = Firebase.firestore.collection("metadata").document("events_update")
private const val TAG = "EventsRepo"
private const val QUERY_LIMIT = 30
private const val MAX_DISTANCE_MTR = 3000f

enum class Queries(
    val query: String,
    val limit: Int
) {
    KL("Events in Kuala Lumpur", QUERY_LIMIT * 0.6.roundToInt()),
    SELANGOR("Events in Selangor", QUERY_LIMIT * 0.3.roundToInt())
}

class EventsRepository(
    val geocodeApi: GeocodeApi,
    val serpApi: SerpApi
) {
    private val codedCachedEvents = mutableMapOf<String, List<Event>>()

    suspend fun loadEvents(
        code: String,
        codeCordMap: Map<String, LatLng>
    ): List<Event> {

        val capturedEvents = mutableListOf<Event>()

        if (shouldUpdateEvents()) {

            val batch = Firebase.firestore.batch()
            var overallQueryCount = 0
            var leftoverQuery = 0

            eventCollectionRef.get().await().documents.forEach { batch.delete(it.reference) }
            batch.commit().await()

            Queries.entries.forEach { query ->
                var queryCount = 0
                var continueQuery = true

                while (continueQuery && queryCount < query.limit + leftoverQuery && overallQueryCount < QUERY_LIMIT) {
                    val events = searchEvents(query.query, queryCount * 10)
                    if (events.size < 10) continueQuery = false
                    queryCount++
                    overallQueryCount++
                    Log.i(TAG, "Querying: ${query.query} + $queryCount")
                    capturedEvents.addAll(events)
                }

                if (queryCount < query.limit) {
                    leftoverQuery = query.limit - queryCount
                }
            }

            coroutineScope {
                // add distinct later
                capturedEvents.forEach { event ->
                    if (event.location == null) {
                        launch {
                            findCoordinate(event)
                        }
                    }
                }
            }

            codeCordMap.forEach { map ->
                capturedEvents.forEach { event ->
                    val results = FloatArray(1)
                    if (event.location?.lat != null && event.location?.lng != null) {
                        Location.distanceBetween(
                            map.value.latitude,
                            map.value.longitude,
                            event.location!!.lat!!,
                            event.location!!.lng!!,
                            results
                        )

                    }
                    if (results.first() <= MAX_DISTANCE_MTR) {
                        if (event.codes == null) event.codes = mutableListOf()
                        event.codes?.add(map.key)
                    }
                }
            }

            capturedEvents.filter { !it.codes.isNullOrEmpty() }.forEach { event ->
                eventCollectionRef.add(event)
            }

            markUpdateComplete()

            codedCachedEvents[code] = capturedEvents.filter {
                it.codes?.contains(code) ?: false
            }

        } else {

            codedCachedEvents[code]?.let {
                return it
            }
            try {
                capturedEvents +=
                    eventCollectionRef
                        .whereArrayContains("codes", code)
                        .get()
                        .await()
                        .documents.mapNotNull { it.toObject<Event>() }.toMutableList()
                codedCachedEvents[code] = capturedEvents
            } catch (e: Exception) {
                Log.e(TAG, "Error loading events: ${e.message}")
            }

        }
        return codedCachedEvents[code] ?: emptyList()
    }
}

private suspend fun EventsRepository.searchEvents(query: String, start: Int): List<Event> {
    return serpApi.getEventInfo(
        query,
        start
    ).let { response ->
        when (response.code()) {
            200 -> {
                readEventInfo(response.body()!!)
            }

            else -> throw Exception("Error getting events: ${response.code()}")
        }
    }
}

private fun readEventInfo(queryResponse: EventQueryResponse): List<Event> {

    when (queryResponse.searchInfo.state) {

        "Fully empty" -> {
            return emptyList()
        }

        "Results for exact spelling" -> {
            return queryResponse.events.map { eventInfo ->
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

private suspend fun EventsRepository.findCoordinate(event: Event) =
    try {
        if (event.address?.get(1) != null) {
            val location = searchCoordinate(
                event.address[0] + ", " + event.address[1]
            )
            location?.let {
                event.location = Coordinate(location.lat, location.lng)
            }
        } else {
            event.location = null
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error saving event: ${e.message}")
    }

private suspend fun EventsRepository.searchCoordinate(address: String): LocationCoordinate? {
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

private suspend fun shouldUpdateEvents(): Boolean {
    return Firebase.firestore.runTransaction { transaction ->

        val snapshot = transaction.get(metadataUpdateRef)

        val lastUpdated = snapshot.getTimestamp("lastUpdated")
        val isUpdating = snapshot.getBoolean("isUpdating") ?: false

        if (isUpdating) return@runTransaction false
        if (lastUpdated != null && !isOlderThan7Days(lastUpdated)) {
            return@runTransaction false
        }

        transaction.update(metadataUpdateRef, "isUpdating", true)
        true
    }.await()
}

private suspend fun markUpdateComplete() {
    metadataUpdateRef.update(
        mapOf(
            "lastUpdated" to Timestamp.now(),
            "isUpdating" to false
        )
    ).await()
}

private fun isOlderThan7Days(timestamp: Timestamp): Boolean {
    val now = System.currentTimeMillis()
    val last = timestamp.toDate().time
    return now - last > 7 * 24 * 60 * 60 * 1000L
}



