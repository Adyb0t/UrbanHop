package com.example.urbanhop.data.navigation_path

data class NavigationPath(
    val startLine: List<String>,
    val interchangeLine: List<String> = emptyList(),
    val lines: List<String>
)