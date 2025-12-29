package com.example.urbanhop.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanhop.data.events.EventsRepository
import com.example.urbanhop.data.event_stations.StationsRepository
import com.example.urbanhop.data.event_stations.getStationCodesAndQueries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel(
    eventsRepository: EventsRepository,
    stationsRepository: StationsRepository,
    val stationCode: String
) : ViewModel() {

    private val _eventsScreenViewState = MutableStateFlow<EventsScreenViewState>(
        EventsScreenViewState.Loading
    )
    val eventsScreenViewState = _eventsScreenViewState.asStateFlow()

    init {
        viewModelScope.launch {
            eventsRepository.loadEvents(
                code = stationCode,
                codeQueryMap = stationsRepository.getStationCodesAndQueries()
            ).also { events ->
                if (events.isEmpty()) {
                    _eventsScreenViewState.value = EventsScreenViewState.Loading
                } else {
                    _eventsScreenViewState.value = EventsScreenViewState.EventList(events)
                }
            }
        }
    }
}