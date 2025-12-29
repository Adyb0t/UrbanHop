package com.example.urbanhop.data.navigation_stations

import android.content.Context
import android.util.Log
import com.example.urbanhop.R
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.InputStream

class TrainNavigationDataSource(val context: Context) {
    private val gson = GsonBuilder().create()
    private val _stations = MutableStateFlow(emptyList<NavigationStation>())
    private val _lines = MutableStateFlow(emptyList<NavigationLine>())
    private val _stationsMap = MutableStateFlow(mutableMapOf<String, NavigationStation>())
    val stations: StateFlow<List<NavigationStation>> = _stations.asStateFlow()
    val lines: StateFlow<List<NavigationLine>> = _lines.asStateFlow()
    val stationsMap: StateFlow<Map<String, NavigationStation>> = _stationsMap.asStateFlow()
    private var stationsLoaded = false
    private var linesLoaded = false
    private var stationsMapLoaded = false

    suspend fun loadStations(): StateFlow<List<NavigationStation>> {
        if (!stationsLoaded) {
            _stations.value = withContext(Dispatchers.IO) {
                context.resources.openRawResource(R.raw.navigation_stations).use { inputStream ->
                    readStations(inputStream)
                }
            }
            stationsLoaded = true
        }
        return stations
    }

    fun mapStations(): StateFlow<Map<String, NavigationStation>> {
        if (stationsLoaded && !stationsMapLoaded) {
            _stations.value.forEach { station ->
                station.names.forEach { name ->
                    _stationsMap.value[name] = station
                }
            }
            stationsMapLoaded = true
        }
        return stationsMap
    }

    suspend fun loadLines(): StateFlow<List<NavigationLine>> {
        if (stationsLoaded && stationsMapLoaded && !linesLoaded) {
            linesLoaded = true
            _lines.value = withContext(Dispatchers.IO) {
                context.resources.openRawResource(R.raw.navigation_lines).use { inputStream ->
                    readLines(inputStream)
                }
            }
        }
        return lines
    }

    private fun readStations(inputStream: InputStream): List<NavigationStation> {
        val stationArray = gson.fromJson(inputStream.reader(), Array<NavigationStationDto>::class.java)
        return stationArray.map { stationDto ->
            NavigationStation(
                id = stationDto.id,
                names = stationDto.names
            )
        }
    }
    private fun readLines(inputStream: InputStream): List<NavigationLine> {
        val lineArray = gson.fromJson(inputStream.reader(), Array<NavigationLineDto>::class.java)
        return lineArray.map { lineDto ->
            Log.d("NavigationDataSource", "Reading line: $lineDto")
            NavigationLine(
                code = lineDto.code,
                name = lineDto.name,
                navStationsName = lineDto.stations,
                navStationsRef = lineDto.stations.map { stationName ->
                    _stationsMap.value[stationName]!!
                }
            )
        }
    }
}

@Serializable
data class NavigationLineDto(
    val code: String,
    val name: String,
    val stations: List<String>
)

@Serializable
data class NavigationStationDto(
    val id: Int,
    val names: List<String>
)