package com.example.urbanhop.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.R
import com.example.urbanhop.navigation.navbar.NavBar
import com.example.urbanhop.screens.ARScreen
import com.example.urbanhop.screens.MapScreen
import com.example.urbanhop.screens.ProfileScreen
import com.kyant.backdrop.backdrops.LayerBackdrop

enum class PageMarker(
    val title: Int,
    val icon: Int,
    val navKey: NavKey
) {
    Ar(
        R.string.ar_string,
        R.drawable.ar_icon,
        ARScreen
    ),
    Home(
        R.string.home_string,
        R.drawable.home_icon,
        MapScreen
    ),
    Account(
        R.string.account_string,
        R.drawable.account_icon,
        ProfileScreen
    )
}

@Composable
fun NavigationUI(
    backdrop: LayerBackdrop,
    backstack: NavBackStack<NavKey>,
    getComponentSize: (IntSize) -> Unit,
    onHomeSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        AnimatedVisibility(
            visible = when (backstack.last()) {
                ARScreen,
                MapScreen,
                ProfileScreen -> true

                else -> false
            },
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            NavBar(
                backstack = backstack,
                backdrop = backdrop,
                getSize = getComponentSize
            ) {
                if (backstack.last() != it.navKey) backstack.add(it.navKey)
                if (it.navKey == MapScreen) {
                    onHomeSelected()
                }
            }
        }
    }
}