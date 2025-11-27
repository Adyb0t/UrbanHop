package com.example.urbanhop.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.R
import com.example.urbanhop.data.events.Event
import com.example.urbanhop.data.stations.Station
import com.example.urbanhop.draw.CustomMarker
import com.example.urbanhop.state.MapScreenViewState
import com.example.urbanhop.state.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object MapScreen : NavKey

private const val PIN_OFFSET = -80

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Map(
    backdrop: LayerBackdrop,
    uiSize: IntSize = IntSize.Zero,
    viewModel: MapViewModel = koinViewModel()
) {
    val mapViewState by viewModel.mapScreenViewState.collectAsStateWithLifecycle()

    var isZoomedToBound by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var openedPin: Station? by remember { mutableStateOf(null) }
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(LatLng(3.2510878785510826, 101.73429681730538), 15f)
    }
    val sheetState = rememberModalBottomSheetState()

    val uiHeight = with(LocalDensity.current) { uiSize.height.toDp() }

    fun LatLng.mapToMap(): IntOffset {
        cameraPositionState.position
        return cameraPositionState.projection
            ?.toScreenLocation(this)
            ?.let { point ->
                IntOffset(point.x + PIN_OFFSET, point.y)
            } ?: IntOffset.Zero
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier
                .layerBackdrop(backdrop)
                .fillMaxSize(),
            googleMapOptionsFactory = {
                GoogleMapOptions().mapId("614fcd75adf6485aba9fb036")
            },
            onMapLoaded = { isMapLoaded = true },
            cameraPositionState = cameraPositionState,
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM,
            contentPadding = PaddingValues(
                bottom = uiHeight + 8.dp,
                top = uiHeight + 8.dp
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false
            )
        )

        when (mapViewState) {
            is MapScreenViewState.Loading -> {}

            is MapScreenViewState.StationList -> {
                val stations = (mapViewState as MapScreenViewState.StationList).stations
                stations.forEach { station ->
                    CustomMarker(
                        modifier = Modifier
                            .offset { station.coordinates.mapToMap() },
                        station,
                        openedPin,
                        onOpened = { station ->
                            openedPin = station
                        },
                        onClickStation = {
                            viewModel.onClickStationLabel(openedPin?.code!!)
                        }
                    )
                }
                isZoomedToBound =
                    zoomToBound(stations, isMapLoaded, isZoomedToBound, cameraPositionState)
            }

            is MapScreenViewState.EventList -> {
                var isBottomSheetVisible by remember { mutableStateOf(false) }
                val events = (mapViewState as MapScreenViewState.EventList).events

                events.forEach { event ->
                    Card(
                        modifier = Modifier
                            .offset {
                                event.location?.let {
                                    LatLng(event.location!!.lat!!, event.location!!.lng!!).mapToMap()
                                }!!
                            }
                            .size(24.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) { }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = uiHeight + 16.dp, horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        Button(
                            modifier = Modifier
                                .size(48.dp),
                            onClick = {
                                viewModel.displayAllStation()
                                openedPin = null
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.DarkGray
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.rounded_close_24),
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            modifier = Modifier
                                .size(48.dp),
                            onClick = {
                                isBottomSheetVisible = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.DarkGray
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.event_icon),
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                    }
                }

                if (isBottomSheetVisible) {
                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            isBottomSheetVisible = false
                        },
                        sheetGesturesEnabled = false,
                    ) {
                        ListEvent(events)
                    }
                }
            }
        }

        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                ContainedLoadingIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentSize()
                )
            }
        }
    }
}

@Composable
private fun ListEvent(events: List<Event>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .border(
                BorderStroke(2.dp, Color.White),
                RoundedCornerShape(20.dp)
            )
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        items(events) {
            Column(
                modifier = Modifier
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
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun zoomToBound(
    stations: List<Station>,
    isMapLoaded: Boolean,
    zoomToBound: Boolean,
    cameraPositionState: CameraPositionState
): Boolean {
    var isZoomedToBound = zoomToBound
    LaunchedEffect(stations, isMapLoaded) {
        if (stations.isNotEmpty() && !isZoomedToBound && isMapLoaded) {
            val boundsBuilder = LatLngBounds.builder()
            for (station in stations) {
                boundsBuilder.include(station.coordinates)
            }
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    160
                ),
                durationMs = 1500
            )
            isZoomedToBound = true
        }
    }
    return isZoomedToBound
}