package com.example.urbanhop.utils

import androidx.compose.ui.unit.dp
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

fun BackdropEffectScope.lightBlur() {
    vibrancy()
    blur(4f.dp.toPx())
    lens(
        16f.dp.toPx(),
        32f.dp.toPx(),
    )
}

fun BackdropEffectScope.heavyBlur() {
    vibrancy()
    blur(10f.dp.toPx())
    lens(
        16f.dp.toPx(),
        32f.dp.toPx(),
    )
}