package com.example.urbanhop.utils

import com.example.urbanhop.data.navigation_path.NavigationPath
import com.example.urbanhop.data.navigation_stations.NavigationLine
import com.example.urbanhop.data.navigation_stations.NavigationStation

private const val COUNT = 2

fun interchangePathNav(
    starting: NavigationStation,
    destination: NavigationStation
): List<NavigationPath> {
    val pathLists = mutableListOf<NavigationPath>()
    var pathCount = 0
    starting.navigationLines.forEach { startLine ->
        val startingIndex = startLine.navStationsRef.indexOf(starting)
        var toZeroIndex = startingIndex
        var toSizeIndex = startingIndex
        var checkAbove = true

        while ((toZeroIndex >= 0 || toSizeIndex < startLine.navStationsRef.size) && pathCount < COUNT) {
            if (checkAbove && toZeroIndex >= 0) {
                if (startLine.navStationsRef[toZeroIndex].navigationLines.size > 1) {
                    startLine.navStationsRef[toZeroIndex].navigationLines.forEach { interchangeLine ->
                        if (destination.navigationLines.contains(interchangeLine)) {
                            val destinationIndex =
                                interchangeLine.navStationsRef.indexOf(destination)
                            val interchangeToIndex =
                                startLine.navStationsRef.indexOf(startLine.navStationsRef[toZeroIndex])
                            val interchangeFromIndex =
                                interchangeLine.navStationsRef.indexOf(startLine.navStationsRef[toZeroIndex])

                            pathLists.add(
                                NavigationPath(
                                    sharedLineNav(
                                        startLine,
                                        starting,
                                        startLine.navStationsRef[interchangeToIndex]
                                    ),
                                    sharedLineNav(
                                        interchangeLine,
                                        interchangeLine.navStationsRef[interchangeFromIndex],
                                        interchangeLine.navStationsRef[destinationIndex]
                                    ),
                                    listOf(
                                        startLine.code,
                                        interchangeLine.code
                                    )
                                )
                            )
                            pathCount++
                        }
                    }
                }
                toZeroIndex--
            }

            if (!checkAbove && toSizeIndex < startLine.navStationsRef.size) {
                if (startLine.navStationsRef[toSizeIndex].navigationLines.size > 1) {
                    startLine.navStationsRef[toSizeIndex].navigationLines.forEach { interchangeLine ->
                        if (destination.navigationLines.contains(interchangeLine)) {
                            val destinationIndex =
                                interchangeLine.navStationsRef.indexOf(destination)
                            val interchangeToIndex =
                                startLine.navStationsRef.indexOf(startLine.navStationsRef[toSizeIndex])
                            val interchangeFromIndex =
                                interchangeLine.navStationsRef.indexOf(startLine.navStationsRef[toSizeIndex])

                            pathLists.add(
                                NavigationPath(
                                    sharedLineNav(
                                        startLine,
                                        starting,
                                        startLine.navStationsRef[interchangeToIndex]
                                    ),
                                    sharedLineNav(
                                        interchangeLine,
                                        interchangeLine.navStationsRef[interchangeFromIndex],
                                        interchangeLine.navStationsRef[destinationIndex]
                                    ),
                                    listOf(
                                        startLine.code,
                                        interchangeLine.code
                                    )
                                )
                            )
                            pathCount++
                        }
                    }
                }
                toSizeIndex++
            }
            checkAbove = !checkAbove
        }
    }

    if (pathLists.isEmpty()) {
        return listOf(
            NavigationPath(
                startLine = listOf("No routes found"),
                lines = emptyList()
            )
        )
    }

    return pathLists
}

fun sharedLineNav(
    sharedLine: NavigationLine,
    from: NavigationStation,
    to: NavigationStation,
): List<String> {
    val stationList = mutableListOf<String>()
    val startingIndex =
        sharedLine.navStationsRef.indexOf(from)
    val destinationIndex =
        sharedLine.navStationsRef.indexOf(to)
    if (startingIndex < destinationIndex) {
        for (i in startingIndex..destinationIndex) {
            stationList.add(sharedLine.navStationsName[i])
        }
    } else {
        for (i in startingIndex downTo destinationIndex) {
            stationList.add(sharedLine.navStationsName[i])
        }
    }
    return stationList
}