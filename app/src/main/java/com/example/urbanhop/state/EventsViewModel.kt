package com.example.urbanhop.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.urbanhop.data.events.EventsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EventsViewModel(
    eventsRepository: EventsRepository
): ViewModel() {

    private val _eventsScreenViewState = MutableStateFlow<EventsScreenViewState>(
        EventsScreenViewState.Loading)
    val eventsScreenViewState = _eventsScreenViewState.asStateFlow()

    init {
        viewModelScope.launch {
            eventsRepository.loadEvents()
            eventsRepository.events.collect { events ->
                if (events.isEmpty()) {
                    _eventsScreenViewState.value = EventsScreenViewState.Loading
                } else {
                    _eventsScreenViewState.value = EventsScreenViewState.EventList(events)
                }
            }
        }
    }
}