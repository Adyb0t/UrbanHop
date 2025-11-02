package com.example.urbanhop.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.draw.StationMarkersMapContent
import com.example.urbanhop.state.MapScreenViewState
import com.example.urbanhop.state.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object MapScreen: NavKey

@Composable
fun Map(
    backdrop: LayerBackdrop,
    uiSize: IntSize = IntSize.Zero,
    viewModel: MapViewModel = koinViewModel()
) {
    var hasInitialZoom by rememberSaveable { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.2510878785510826, 101.73429681730538), 6f)
    }
    val scope = rememberCoroutineScope()
    val uiHeight = with(LocalDensity.current) { uiSize.height.toDp() }
    val mapViewState by viewModel.mapScreenViewState.collectAsStateWithLifecycle()

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
                bottom =  uiHeight + 8.dp,
                top = uiHeight + 8.dp
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false
            )
        ) {
            when(mapViewState) {
                is MapScreenViewState.Loading -> {}
                is MapScreenViewState.StationList -> {
                    StationMarkersMapContent(
                        stations = (mapViewState as MapScreenViewState.StationList).stations)
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
                CircularProgressIndicator(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .wrapContentSize()
                )
            }
        } else {
            LaunchedEffect(hasInitialZoom) {
                if (!hasInitialZoom) {
                    zoom(scope, cameraPositionState)
                    hasInitialZoom = true
                }
            }
        }
    }
}

fun zoom(
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState,
) {
    scope.launch {
        cameraPositionState.animate(
            update = CameraUpdateFactory.zoomBy(3.5f),
            durationMs = 1000
        )
    }
}