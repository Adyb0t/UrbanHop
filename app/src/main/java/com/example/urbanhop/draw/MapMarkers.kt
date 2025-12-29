package com.example.urbanhop.draw

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.urbanhop.R
import com.example.urbanhop.data.event_stations.EventStation
import com.example.urbanhop.ui.theme.UrbanHopTheme

@Composable
fun CustomMarker(
    modifier: Modifier,
    eventStation: EventStation,
    openedPin: EventStation?,
    onOpened: (EventStation?) -> Unit,
    onClickStation: () -> Unit
) {
    var isOpened = openedPin == eventStation
    Row(
        modifier = modifier
            .height(48.dp)
    ) {
        Button(
            modifier = Modifier
                .size(48.dp),
            onClick = {
                onOpened(eventStation)
            },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_train),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        if (isOpened) Spacer(Modifier.width(4.dp))
        AnimatedVisibility(
            visible = isOpened
        ) {
            Row(
                modifier = Modifier
                    .clickable(
                        enabled = isOpened,
                        onClick = {
                            onClickStation()
                            isOpened = false
                        }
                    )
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    text = eventStation.name,
                    modifier = Modifier
                        .height(48.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.secondary
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

@Composable
fun CustomMarkerStatic(
    modifier: Modifier,
    eventStation: EventStation,
) {
    Button(
        modifier = modifier
            .size(48.dp),
        onClick = {},
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
}

@Preview
@Composable
private fun Prev() {
    UrbanHopTheme {
        Box() {
//            CustomMarker(
//                modifier = Modifier,
//                station = Station("test", "test", "test", LatLng(1.1, 1.2), "test")
//            )
        }
    }
}

