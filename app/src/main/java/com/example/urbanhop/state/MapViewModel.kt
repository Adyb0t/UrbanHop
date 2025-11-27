package com.example.urbanhop.state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanhop.data.events.EventsRepository
import com.example.urbanhop.data.location.GeocodeApi
import com.example.urbanhop.data.stations.StationsRepository
import com.example.urbanhop.data.stations.getStationCodesAndQueries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val stationsRepository: StationsRepository,
    private val eventsRepository: EventsRepository
) : ViewModel() {
    private val _mapScreenViewState =
        MutableStateFlow<MapScreenViewState>(MapScreenViewState.Loading)
    val mapScreenViewState = _mapScreenViewState.asStateFlow()

    init {
        viewModelScope.launch {
            stationsRepository.loadStations()
            displayAllStation()
        }
    }

    internal fun onClickStationLabel(code: String) {
        viewModelScope.launch {
            Log.d("MapViewModel", "onClickStationLabel: $code")
            eventsRepository.loadEvents(code, stationsRepository.getStationCodesAndQueries())
                .also { events ->
                    _mapScreenViewState.value = MapScreenViewState.EventList(events)
                }
        }
    }

    internal fun displayAllStation() {
        viewModelScope.launch {
            stationsRepository.stations.collect { stations ->
                if (stations.isEmpty()) {
                    _mapScreenViewState.value = MapScreenViewState.Loading
                } else {
                    _mapScreenViewState.value = MapScreenViewState.StationList(stations)
                }
            }
        }
    }
}