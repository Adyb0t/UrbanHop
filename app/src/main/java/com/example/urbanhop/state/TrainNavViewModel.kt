package com.example.urbanhop.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanhop.data.navigation_stations.TrainNavigationDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TrainNavViewModel(
    private val trainNavigationDataSource: TrainNavigationDataSource,
    private val selected: String
) : ViewModel() {
    private val _trainNavScreenViewState = MutableStateFlow<TrainNavScreenViewState>(
        TrainNavScreenViewState.Loading
    )
    val trainNavScreenViewState = _trainNavScreenViewState.asStateFlow()

    init {
        viewModelScope.launch {
            trainNavigationDataSource.loadStations()
            trainNavigationDataSource.mapStations()
            trainNavigationDataSource.loadLines()
            queryPage()
        }
    }

    fun onBack() {
        viewModelScope.launch {
            queryPage()
        }
    }

    private suspend fun queryPage() {
        trainNavigationDataSource.stations.combine(trainNavigationDataSource.stationsMap) { stations, stationRef ->
            if (stations.isEmpty()) {
                TrainNavScreenViewState.Loading
            } else {
                TrainNavScreenViewState.DirectionQuery(
                    selected = selected,
                    stations = stations,
                    stationsRef = stationRef
                )
            }
        }.collect { newState ->
            _trainNavScreenViewState.value = newState
        }
    }

    internal fun onConfirmStation() {
        viewModelScope.launch {
            combine(
                trainNavigationDataSource.stations,
                trainNavigationDataSource.lines,
                trainNavigationDataSource.stationsMap
            ) { stations, lines, stationRef ->
                if (stations.isEmpty() || lines.isEmpty()) {
                    TrainNavScreenViewState.Loading
                } else {
                    TrainNavScreenViewState.NavigationInfo(
                        stations = stations,
                        lines = lines,
                        stationsRef = stationRef
                    )
                }
            }.collect { newState ->
                _trainNavScreenViewState.value = newState
            }
        }
    }
}