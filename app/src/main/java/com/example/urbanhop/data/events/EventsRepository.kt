package com.example.urbanhop.data.events

import android.content.Context
import android.util.Log
import com.example.urbanhop.R
import com.example.urbanhop.data.location.GeocodeApi
import com.example.urbanhop.data.location.Location
import com.google.android.gms.maps.model.LatLng
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
import kotlin.collections.plusAssign

private val eventCollectionRef = Firebase.firestore.collection("events")
private const val isNotWeeklyUpdate = true
private const val TAG = "EventsRepo"

class EventsRepository(
    val context: Context,
    val geocodeApi: GeocodeApi
) {
    internal val gson = GsonBuilder().create()
    private val codedCachedEvents = mutableMapOf<String, List<Event>>()

    suspend fun loadEvents(
        code: String,
        codeQueryMap: Map<String, String>,
    ): List<Event> {

        val capturedEvents = mutableListOf<Event>()

        if (isNotWeeklyUpdate) {
            codedCachedEvents[code]?.let {
                return it
            }
            try { //if already updated, load from firebase
                capturedEvents +=
                    eventCollectionRef
                        .whereEqualTo("code", code)
                        .get()
                        .await()
                        .documents.mapNotNull { it.toObject<Event>() }.toMutableList()
                codedCachedEvents[code] = capturedEvents
            } catch (e: Exception) {
                Log.e(TAG, "Error loading events: ${e.message}")
            }
        } else { //update firebase weekly
            val batch = Firebase.firestore.batch()
            eventCollectionRef.get().await().documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
            codeQueryMap.forEach { pair ->
                //API query simulation
                Log.d(TAG, pair.toString())
                val eventPerCode = context.resources.openRawResource(
                    when (pair.key) {
                        "MBB" -> {
                            Log.i(TAG, "querying MBB: events near ${pair.value}")
                            R.raw.events_bukit_bintang
                        }

                        "LKC" -> {
                            Log.i(TAG, "querying LKC: events near ${pair.value}")
                            R.raw.events_klcc
                        }

                        "LKS" -> {
                            Log.i(TAG, "querying LKS: events near ${pair.value}")
                            R.raw.events_kl_sentral
                        }

                        "LPS" -> {
                            Log.i(TAG, "querying LPS: events near ${pair.value}")
                            R.raw.events_pasar_seni
                        }

                        "MMD" -> {
                            Log.i(TAG, "querying MMD: events near ${pair.value}")
                            R.raw.events_mutiara_damansara
                        }

                        "LWM" -> {
                            Log.i(TAG, "querying LWM: events near ${pair.value}")
                            R.raw.events_wangsa_maju
                        }

                        "L15" -> {
                            Log.i(TAG, "querying L15: events near ${pair.value}")
                            R.raw.events_ss15
                        }

                        "MKG" -> {
                            Log.i(TAG, "querying MKG: events near ${pair.value}")
                            R.raw.events_kajang
                        }

                        else -> throw Exception("Unknown code")
                    }
                ).use { inputStream ->
                    readEventInfo(inputStream)
                }
                eventPerCode.forEach {
                    if (it.codes == null) it.codes = mutableListOf()
                    it.codes?.add(pair.key)
                }
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
            codedCachedEvents[code] = capturedEvents.distinct()
        }
        codedCachedEvents[code]?.forEach {
            Log.i(
                TAG,
                "Event: ${it.title} | ${it.date} | ${it.address} | ${it.location?.lat}, ${it.location?.lng}"
            )
        }
        return codedCachedEvents[code] ?: emptyList()
    }

    suspend fun loadEvents2(
        code: String,
        codeCoordMap: Map<String, LatLng>,
    ): List<Event> {

        val capturedEvents = mutableListOf<Event>()

        if (isNotWeeklyUpdate) {
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
        else
        {
            return codedCachedEvents[code] ?: emptyList()
        }
        return codedCachedEvents[code] ?: emptyList()
    }
}

private fun EventsRepository.readEventInfo(inputStream: InputStream): List<Event> {
    val eventInfoList = gson.fromJson(inputStream.reader(), EventInfo::class.java)
    when (eventInfoList.searchInfo.state) {

        "Fully empty" -> {
            return emptyList()
        }

        "Results for exact spelling" -> {
            return eventInfoList.events.map { eventInfo ->
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



