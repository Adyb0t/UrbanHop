package com.example.urbanhop.state

import com.example.urbanhop.data.navigation_stations.NavigationLine
import com.example.urbanhop.data.navigation_stations.NavigationStation

sealed interface TrainNavScreenViewState {
    data object Loading: TrainNavScreenViewState
    data class DirectionQuery(
        val selected: String,
        val stations: List<NavigationStation>,
        val stationsRef: Map<String, NavigationStation>
    ): TrainNavScreenViewState
    data class NavigationInfo(
        val stations: List<NavigationStation>,
        val lines: List<NavigationLine>,
        val stationsRef: Map<String, NavigationStation>
    ): TrainNavScreenViewState
}