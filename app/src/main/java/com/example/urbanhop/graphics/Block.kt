package com.example.urbanhop.graphics

import androidx.compose.runtime.Composable

@Composable
fun Block(content: @Composable () -> Unit) {
    content()
}