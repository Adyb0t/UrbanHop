package com.example.urbanhop.draw

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanhop.R
import com.example.urbanhop.data.event_stations.EventStation

@Composable
fun CustomMarker(
    modifier: Modifier,
    eventStation: EventStation,
    openedPin: EventStation?,
    onOpened: (EventStation?) -> Unit,
    onClickStation: () -> Unit
) {
    var isOpened = openedPin == eventStation
    val colorChangePrimary by animateColorAsState(
        targetValue = if (isOpened) colorScheme.primary else colorScheme.primaryContainer
    )
    val colorChangeOnPrimary by animateColorAsState(
        targetValue = if (isOpened) colorScheme.onPrimary else colorScheme.onPrimaryContainer
    )
    val spacerChange by animateDpAsState(
        targetValue = if (isOpened) 4.dp else 0.dp
    )
    val infinite = rememberInfiniteTransition()
    val scale by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = modifier
            .height(48.dp)
    ) {
        Button(
            modifier = Modifier
                .size(48.dp)
                .border(
                    width = if (isOpened) 0.dp else 1.dp,
                    color = colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ),
            onClick = {
                onOpened(eventStation)
            },
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonColors(
                containerColor = colorChangePrimary,
                contentColor = colorChangeOnPrimary,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Icon(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = if (openedPin == null) scale else 1f
                        scaleY = if (openedPin == null) scale else 1f
                    },
                painter = painterResource(id = R.drawable.outline_train),
                contentDescription = null,
                tint = colorChangeOnPrimary
            )
        }
        Spacer(Modifier.width(spacerChange))
        AnimatedVisibility(
            visible = isOpened,
        ) {
            Column(
                modifier = Modifier
                    .height(48.dp)
                    .clickable(
                        enabled = isOpened,
                        onClick = {
                            onClickStation()
                            isOpened = false
                        },
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = colorScheme.onSurface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .offset(y = 4.dp),
                    text = eventStation.name,
                    color = colorScheme.onSurface,
                    fontSize = 18.sp
                )
                Text(
                    modifier = Modifier
                        .offset(y = (-4).dp),
                    text = "Click to search for events",
                    color = colorScheme.onSurface.copy(0.75f),
                    fontSize = 14.sp
                )
            }
        }
        Spacer(Modifier.width(spacerChange))
        AnimatedVisibility(
            visible = isOpened,
        ) {
            Button(
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        shape = RoundedCornerShape(12.dp),
                        width = 1.dp,
                        color = colorScheme.onSurface
                    ),
                onClick = {
                    isOpened = false
                    onOpened(null)
                },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonColors(
                    containerColor = colorScheme.surface,
                    contentColor = colorScheme.onSurface,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.DarkGray
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_close_24),
                    contentDescription = null,
                    tint = colorScheme.onSurface
                )
            }
        }
    }
}

