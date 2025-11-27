package com.example.urbanhop.state

import com.example.urbanhop.data.events.Event
import com.example.urbanhop.data.stations.Station

sealed interface MapScreenViewState {
    data object Loading: MapScreenViewState
    data class StationList(
        val stations: List<Station>
    ) : MapScreenViewState
    data class EventList(
        val events: List<Event>
    ) : MapScreenViewState
}