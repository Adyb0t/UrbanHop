package com.example.urbanhop.draw

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.urbanhop.R
import com.example.urbanhop.data.stations.Station
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
@GoogleMapComposable
fun StationMarkersMapContent(
    stations: List<Station>,
    onStationClick: (Marker) -> Boolean = { false }
) {
    stations.forEach { station ->
        Marker(
            state = rememberUpdatedMarkerState(position = station.coordinates),
            title = station.name,
            tag = station,
            onClick = {
                onStationClick(it)
                false
            },
            anchor = Offset(0.5f, 0.5f),
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        )
//        Icon(
//            painterResource(id = R.drawable.baseline_filter_hdr_24),
//            tint = Color.Black,
//            contentDescription = "",
//            modifier = Modifier
//                .size(32.dp)
//                .padding(1.dp)
//                .drawBehind {
//                    drawCircle(color = Color.White, style = Fill)
//                    drawCircle(color = Color.Black, style = Stroke(width = 3f))
//                }
//                .padding(4.dp)
//        )
    }
}

