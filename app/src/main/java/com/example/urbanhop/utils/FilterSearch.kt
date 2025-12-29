package com.example.urbanhop.utils

import com.example.urbanhop.navigation.searchbar.CANDIDATE_COUNT
import com.example.urbanhop.navigation.searchbar.SUGGESTION_COUNT
import org.simmetrics.metrics.JaroWinkler

private val stringMetric = JaroWinkler()

fun filterAndRank(
    unFilteredList: List<String>,
    query: String
): List<String> {
    if (query.length < 2) {
        return emptyList()
    } else {
        val q = query.lowercase()
        return unFilteredList
            .asSequence()
            .filter { it.lowercase().contains(q) }
            .take(CANDIDATE_COUNT)
            .toList()
            .map { it to stringMetric.compare(q, it) }
            .sortedByDescending { it.second }
            .take(SUGGESTION_COUNT)
            .map { it.first }
    }
}

fun filterAndRank(
    unFilteredSet: Set<String>,
    query: String
): List<String> {
    if (query.length < 2) {
        return emptyList()
    } else {
        val q = query.lowercase()
        return unFilteredSet
            .asSequence()
            .filter { it.lowercase().contains(q) }
            .take(CANDIDATE_COUNT)
            .toList()
            .map { it to stringMetric.compare(q, it) }
            .sortedByDescending { it.second }
            .take(SUGGESTION_COUNT)
            .map { it.first }
    }
}