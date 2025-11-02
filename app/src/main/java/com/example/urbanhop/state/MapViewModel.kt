package com.example.urbanhop.state

import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanhop.data.stations.StationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    stationsRepository: StationsRepository
) : ViewModel() {
    private val _mapScreenViewState = MutableStateFlow<MapScreenViewState>(MapScreenViewState.Loading)
    val mapScreenViewState = _mapScreenViewState.asStateFlow()

    init {
        viewModelScope.launch {
            stationsRepository.loadStations()
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