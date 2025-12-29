package com.example.urbanhop

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.urbanhop.navigation.NavigationUI
import com.example.urbanhop.screens.AR
import com.example.urbanhop.screens.ARScreen
import com.example.urbanhop.screens.Map
import com.example.urbanhop.screens.MapScreen
import com.example.urbanhop.screens.Profile
import com.example.urbanhop.screens.ProfileScreen
import com.example.urbanhop.screens.TrainNav
import com.example.urbanhop.screens.TrainNavScreen
import com.example.urbanhop.ui.theme.UrbanHopTheme
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UrbanHopTheme {
                val backStack = rememberNavBackStack(MapScreen)
                var uiSize by remember { mutableStateOf(IntSize.Zero) }
                val backdrop = rememberLayerBackdrop()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    NavDisplay(
                        backStack = backStack,
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        entryProvider = { key ->
                            when (key) {

                                is MapScreen -> {
                                    NavEntry(key) {
                                        Map(
                                            backdrop,
                                            backStack,
                                            uiSize
                                        )
                                    }
                                }

                                is ARScreen -> {
                                    NavEntry(key) { AR() }
                                }

                                is ProfileScreen -> {
                                    NavEntry(key) { Profile() }
                                }

                                is TrainNavScreen -> {
                                    NavEntry(key) {
                                        TrainNav(
                                            koinViewModel { parametersOf(key.selected) }
                                        )
                                    }
                                }

                                else -> throw RuntimeException("Unknown key: $key")
                            }
                        },
                    )

                    NavigationUI(
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

