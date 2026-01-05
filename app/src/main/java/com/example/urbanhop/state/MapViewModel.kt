package com.example.urbanhop.state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanhop.data.events.EventsRepository
import com.example.urbanhop.data.event_stations.StationsRepository
import com.example.urbanhop.data.event_stations.getStationCodesAndQueries
import com.example.urbanhop.data.navigation_stations.TrainNavigationDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val stationsRepository: StationsRepository,
    private val eventsRepository: EventsRepository,
    private val trainNavDataSource: TrainNavigationDataSource
) : ViewModel() {
    private val _mapScreenViewState =
        MutableStateFlow<MapScreenViewState>(MapScreenViewState.LoadingPage)
    val mapScreenViewState = _mapScreenViewState.asStateFlow()
    private val stationsCodeQueryMap = stationsRepository.getStationCodesAndQueries()

    init {
        viewModelScope.launch {
            stationsRepository.loadStations()
            displayAllStation()
        }
    }

    internal fun onClickStationLabel(code: String) {
        viewModelScope.launch {
            _mapScreenViewState.value = MapScreenViewState.LoadingEvents
            delay(3000)
            Log.i("MapViewModel", "Selected Station: $code")
            eventsRepository.loadEvents(code, stationsCodeQueryMap).also { events ->
                _mapScreenViewState.value = MapScreenViewState.EventList(events)
            }
        }
    }

    internal fun displayAllStation() {
        viewModelScope.launch {
            stationsRepository.stations.collect { stations ->
                if (stations.isEmpty()) {
                    _mapScreenViewState.value = MapScreenViewState.LoadingPage
                } else {
                    _mapScreenViewState.value = MapScreenViewState.StationList(stations)
                }
            }
        }
    }

    internal fun onExpandedSearch() {
        viewModelScope.launch {
            trainNavDataSource.loadStations()
            trainNavDataSource.stations.collect { stations ->
                _mapScreenViewState.value = MapScreenViewState.ExpandedSearch(stations)
            }
        }
    }
}