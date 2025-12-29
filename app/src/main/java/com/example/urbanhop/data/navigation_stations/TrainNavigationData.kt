package com.example.urbanhop.data.navigation_stations

data class NavigationStation(
    val id: Int,
    val names: List<String>
) {
    val navigationLines: MutableSet<NavigationLine> = mutableSetOf()
}

data class NavigationLine(
    val code: String,
    val name: String,
    val navStationsName: List<String>,
    val navStationsRef: List<NavigationStation>
) {
    init {
        navStationsRef.forEach { it.navigationLines.add(this) }
    }
}