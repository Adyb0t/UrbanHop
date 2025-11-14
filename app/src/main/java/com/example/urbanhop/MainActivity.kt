package com.example.urbanhop

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.urbanhop.navigation.NavigationUI
import com.example.urbanhop.navigation.Pages
import com.example.urbanhop.screens.AR
import com.example.urbanhop.screens.ARScreen
import com.example.urbanhop.screens.Events
import com.example.urbanhop.screens.EventsScreen
import com.example.urbanhop.screens.Map
import com.example.urbanhop.screens.MapScreen
import com.example.urbanhop.screens.Profile
import com.example.urbanhop.screens.ProfileScreen
import com.example.urbanhop.ui.theme.UrbanHopTheme
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UrbanHopTheme {
                val backStack = rememberNavBackStack(Pages.Home.navKey)
                var uiSize by remember { mutableStateOf(IntSize.Zero) }
                val backdrop = rememberLayerBackdrop()
                val textFieldState = rememberTextFieldState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    NavDisplay(
                        backStack = backStack,
                        entryProvider = { key ->
                            when (key) {
                                MapScreen -> {
                                    NavEntry(key) { Map(backdrop, uiSize, backStack) }
                                }
                                ARScreen -> {
                                    NavEntry(key) { AR() }
                                }
                                ProfileScreen -> {
                                    NavEntry(key) { Profile() }
                                }
                                EventsScreen -> {
                                    NavEntry(key) { Events() }
                                }
                                else -> throw RuntimeException("Unknown key: $key")
                            }
                        },
                    )
                    NavigationUI(
                        textFieldState,
                        backdrop,
                        backStack,
                    ) { size ->
                        uiSize = size
                    }
                }
            }
        }
    }
}

fun onSearch(query: String) {

}