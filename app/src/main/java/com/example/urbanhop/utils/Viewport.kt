package com.example.urbanhop.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

@Stable
data class ViewportSize(
    val px: IntSize,
    val width: androidx.compose.ui.unit.Dp,
    val height: androidx.compose.ui.unit.Dp
)

@Composable
fun rememberViewportSize(): Pair<ViewportSize?, Modifier> {
    val density = LocalDensity.current
    var sizePx by remember { mutableStateOf<IntSize?>(null) }

    val modifier = Modifier.onSizeChanged { sizePx = it }

    val viewport = sizePx?.let {
        with(density) {
            ViewportSize(
                px = it,
                width = it.width.toDp(),
                height = it.height.toDp()
            )
        }
    }

    return viewport to modifier
}