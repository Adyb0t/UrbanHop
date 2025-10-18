package com.example.urbanhop.graphics

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.requireDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize

context(DrawScope, DelegatableNode)
internal fun recordLayer(
    layer: GraphicsLayer,
    size: IntSize = this@DrawScope.size.toIntSize(),
//    size: IntSize = size.toIntSize(),
    block: DrawScope.() -> Unit
) {
    layer.record(
        density = requireDensity(),
        layoutDirection = layoutDirection,
        size = size
    ) {
        draw(
            density = drawContext.density,
            layoutDirection = drawContext.layoutDirection,
            canvas = drawContext.canvas,
            size = drawContext.size,
            graphicsLayer = drawContext.graphicsLayer,
            block = block
        )
    }
}