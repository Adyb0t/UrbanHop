package com.example.urbanhop.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntSize
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.R
import com.example.urbanhop.navigation.navbar.NavBar
import com.example.urbanhop.navigation.searchbar.SearchBarCustom
import com.example.urbanhop.screens.ARScreen
import com.example.urbanhop.screens.EventsScreen
import com.example.urbanhop.screens.MapScreen
import com.example.urbanhop.screens.ProfileScreen
import com.kyant.backdrop.backdrops.LayerBackdrop

enum class Pages(
    val title: Int,
    val navKey: NavKey
) {
    Ar(
        R.string.ar_string,
        ARScreen
    ),
    Home(
        R.string.home_string,
        MapScreen
    ),
    Account(
        R.string.account_string,
        ProfileScreen
    ),
    Events(
        R.string.events_string,
        EventsScreen
    )
}

enum class PageMarker(
    val title: Int,
    val icon: Int,
    val navKey: NavKey
) {
    Ar(
        R.string.ar_string,
        R.drawable.ar_icon,
        Pages.Ar.navKey
    ),
    Home(
        R.string.home_string,
        R.drawable.home_icon,
        Pages.Home.navKey

    ),
    Account(
        R.string.account_string,
        R.drawable.account_icon,
        Pages.Account.navKey
    )
}

@Composable
fun NavigationUI(
    textFieldState: TextFieldState,
    backdrop: LayerBackdrop,
    backstack: NavBackStack<NavKey>,
    getComponentSize: (IntSize) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        AnimatedVisibility(
            visible = backstack.last() == Pages.Home.navKey,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            label = "SearchBarAnimation"
        ) {
            SearchBarCustom(
                modifier = Modifier.align(Alignment.TopCenter),
                textFieldState = textFieldState,
                backdrop = backdrop,
                keyboardController = keyboardController
            )
        }
        AnimatedVisibility(
            visible = when (backstack.last()) {
                Pages.Ar.navKey,
                Pages.Home.navKey,
                Pages.Account.navKey -> true
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
                if (it.navKey == Pages.Home.navKey) {
                    backstack.clear()
                    backstack.add(Pages.Home.navKey)
                }
            }
        }
    }
}