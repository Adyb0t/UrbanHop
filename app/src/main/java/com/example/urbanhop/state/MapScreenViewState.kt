package com.example.urbanhop.state

import com.example.urbanhop.data.events.Event
import com.example.urbanhop.data.event_stations.EventStation
import com.example.urbanhop.data.navigation_stations.NavigationStation

sealed interface MapScreenViewState {
    data object LoadingPage: MapScreenViewState
    data object LoadingEvents: MapScreenViewState
    data class StationList(
        val eventStations: List<EventStation>
    ) : MapScreenViewState
    data class EventList(
        val events: List<Event>
    ) : MapScreenViewState
    data class ExpandedSearch(
        val stations: List<NavigationStation>
    ) : MapScreenViewState
}