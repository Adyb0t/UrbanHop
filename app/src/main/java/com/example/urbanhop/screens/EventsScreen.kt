package com.example.urbanhop.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.state.EventsScreenViewState
import com.example.urbanhop.state.EventsViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.collectAsState

//@Serializable
//data class EventsScreen(val stationCode: String) : NavKey

@Composable
fun Events(
    viewModel: EventsViewModel = koinViewModel()
) {
    val eventsViewState by viewModel.eventsScreenViewState.collectAsStateWithLifecycle()
    Log.d("EventComp",viewModel.stationCode)

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn {
                when (eventsViewState) {
                    is EventsScreenViewState.Loading -> {
                        Log.d("EventComp","Loading")

                        items(15) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth()
                                    .height(96.dp)
                                    .clip(
                                        RoundedCornerShape(12.dp)
                                    )
                                    .background(Color.DarkGray)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) { }
                        }
                    }
                    is EventsScreenViewState.EventList -> {
                        val events = (eventsViewState as EventsScreenViewState.EventList).events
                        events[0].code?.let { Log.d("Events", it) }
                        Log.d("EventComp", events.size.toString())

                        items(events) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(12.dp)
                                    )
                                    .background(Color.White)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = it.title ?: "No title"
                                    )
                                }
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = it.date ?: "Unspecified date",
                                        modifier = Modifier.weight(1.0F)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = it.venue?.name ?: "No specified venue",
                                        modifier = Modifier.weight(1.0F),
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}