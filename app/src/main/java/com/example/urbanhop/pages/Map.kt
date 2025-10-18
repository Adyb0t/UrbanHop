package com.example.urbanhop.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.urbanhop.graphics.Backdrop
import com.example.urbanhop.graphics.backdrops.LayerBackdrop
import com.example.urbanhop.graphics.backdrops.layerBackdrop
import com.example.urbanhop.graphics.backdrops.rememberLayerBackdrop
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun Map(paddingUI: PaddingValues, backdrop: LayerBackdrop) {
    var isMapLoaded by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(3.2510878785510826, 101.73429681730538), 12f)
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
            contentPadding = PaddingValues(bottom = paddingUI.calculateBottomPadding() + 80.dp)
        ) {}
    }
}