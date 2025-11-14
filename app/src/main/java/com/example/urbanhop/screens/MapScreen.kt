package com.example.urbanhop.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
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
import com.google.maps.android.compose.rememberCameraPositionState
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object MapScreen : NavKey

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Map(
    backdrop: LayerBackdrop,
    uiSize: IntSize = IntSize.Zero,
    backStack: NavBackStack<NavKey>,
    viewModel: MapViewModel = koinViewModel()
) {
    val mapViewState by viewModel.mapScreenViewState.collectAsStateWithLifecycle()
    var isZoomedToBound by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(LatLng(3.2510878785510826, 101.73429681730538), 15f)
    }
    val uiHeight = with(LocalDensity.current) { uiSize.height.toDp() }

    fun LatLng.mapToMap(): IntOffset {
        cameraPositionState.position
        return cameraPositionState.projection
            ?.toScreenLocation(this)
            ?.let { point ->
                IntOffset(point.x, point.y)
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
                var openedPin: Station? by remember { mutableStateOf(null) }
                stations.forEach { station ->
                    CustomMarker(
                        modifier = Modifier
                            .offset { station.coordinates.mapToMap() },
                        station,
                        cameraPositionState,
                        backStack,
                        openedPin,
                        onOpened = { station ->
                            openedPin = station
                        }
                    )
                }
                isZoomedToBound = zoomToBound(stations, isMapLoaded, isZoomedToBound, cameraPositionState)
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