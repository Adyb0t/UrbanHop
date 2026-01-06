package com.example.urbanhop.data.event_stations

import android.content.Context
import com.example.urbanhop.R
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.InputStream

class StationsRepository(val context: Context) {
    private val gson = GsonBuilder().create()
    private val _stations = MutableStateFlow(emptyList<EventStation>())
    private val _stationsMap = MutableStateFlow(emptyMap<String, String>())
    val stations: StateFlow<List<EventStation>> = _stations.asStateFlow()
    val stationsMap: StateFlow<Map<String, String>> = _stationsMap.asStateFlow()
    private var loaded = false

    suspend fun loadStations(): StateFlow<List<EventStation>> {
        if (!loaded) {
            loaded = true
            _stations.value = withContext(Dispatchers.IO) {
                context.resources.openRawResource(R.raw.event_stations).use { inputStream ->
                    readLocations(inputStream)
                }
            }
        }
        return stations
    }

    private fun readLocations(inputStream: InputStream): List<EventStation> {
        val locationArray = gson.fromJson(inputStream.reader(), Array<Location>::class.java)
        return locationArray.map { location ->
            EventStation(
                name = location.name,
                queryName = location.queryName,
                address = location.address,
                coordinates = LatLng(
                    location.coordinates.latitude,
                    location.coordinates.longitude
                ),
                code = location.code
            )
        }
    }

    fun getStationCodesAndQueries(): StateFlow<Map<String, String>> {
        _stationsMap.value = _stations.value.associate { it.code to it.queryName }
        return stationsMap
    }
}

private data class Location(
    val name: String,
    @SerializedName("query_name")
    val queryName: String,
    val address: String,
    val coordinates: Coordinates,
    val code: String
)

private data class Coordinates(
    val latitude: Double,
    val longitude: Double
)
