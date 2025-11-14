package com.example.urbanhop.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas

fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

fun DrawScope.drawCrescent(
    xPosition: Float,
    flipped: Boolean,
    crescentOffset: Float,
    crescentRadius: Float
) {
    drawWithLayer {
        val offset = if (flipped) -crescentOffset else crescentOffset

        drawCircle(
            color = Color.White.copy(alpha = 0.25f),
            radius = crescentRadius,
            center = Offset(x = xPosition, y = size.height / 2f)
        )
        drawCircle(
            color = Color.Transparent,
            radius = crescentRadius,
            center = Offset(
                x = xPosition + offset,
                y = size.height / 2f
            ),
            blendMode = BlendMode.Clear
        )
    }
}