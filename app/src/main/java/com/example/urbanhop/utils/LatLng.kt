package com.example.urbanhop.utils

import androidx.compose.ui.unit.IntOffset
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

fun LatLng.mapToMap(xOffset: Int = 0, yOffset: Int = 0, cameraPositionState: CameraPositionState): IntOffset {
    cameraPositionState.position
    return cameraPositionState.projection
        ?.toScreenLocation(this)
        ?.let { point ->
            IntOffset(
                point.x + xOffset,
                point.y + yOffset
            )
        } ?: IntOffset.Zero
}