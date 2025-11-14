package com.example.urbanhop.state

import com.example.urbanhop.data.events.Event

sealed interface EventsScreenViewState {
    data object Loading: EventsScreenViewState
    data class EventList(
        val events: List<Event>
    ): EventsScreenViewState
}