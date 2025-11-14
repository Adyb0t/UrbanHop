package com.example.urbanhop.draw

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.R
import com.example.urbanhop.data.stations.Station
import com.example.urbanhop.screens.EventsScreen
import com.example.urbanhop.ui.theme.UrbanHopTheme
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

@Composable
fun CustomMarker(
    modifier: Modifier,
    station: Station,
    cameraPositionState: CameraPositionState? = null,
    backStack: NavBackStack<NavKey>? = null,
    openedPin: Station? = null,
    onOpened: (Station?) -> Unit = nulled@{}
) {
    var isOpened = openedPin == station
    Row(
        modifier = modifier
            .height(48.dp)
    ) {
        Button(
            modifier = Modifier
                .size(48.dp),
            onClick = {
                onOpened(station)
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
                painter = painterResource(id = R.drawable.outline_train),
                contentDescription = null,
                tint = Color.Black
            )
        }
        if (isOpened) Spacer(Modifier.width(4.dp))
        AnimatedVisibility(
            visible = isOpened
        ) {
            Row {
                Text(
                    text = station.name,
                    modifier = Modifier
                        .height(48.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = Color.White,
                                cornerRadius = CornerRadius(12.dp.toPx())
                            )
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable(
                            enabled = isOpened,
                            onClick = {
                                backStack?.add(EventsScreen)
                                isOpened = false
                            }
                        )
                )
            }
        }
        if (isOpened) Spacer(Modifier.width(4.dp))
        AnimatedVisibility(
            visible = isOpened
        ) {
            Button(
                modifier = Modifier
                    .size(48.dp),
                onClick = {
                    isOpened = false
                    onOpened(null)
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
        }
    }
}

@Preview
@Composable
private fun Prev() {
    UrbanHopTheme {
        Box() {
            CustomMarker(
                modifier = Modifier,
                station = Station("test", "test", LatLng(1.1, 1.2))
            )
        }
    }
}

