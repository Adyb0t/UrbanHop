package com.example.urbanhop.state

import com.example.urbanhop.data.stations.Station

sealed interface MapScreenViewState {
    data object Loading: MapScreenViewState
    data class StationList(
        val stations: List<Station>
    ) : MapScreenViewState
}