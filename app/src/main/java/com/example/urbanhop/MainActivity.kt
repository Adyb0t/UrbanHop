package com.example.urbanhop

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.urbanhop.screens.AR
import com.example.urbanhop.screens.ARScreen
import com.example.urbanhop.screens.Map
import com.example.urbanhop.screens.MapScreen
import com.example.urbanhop.screens.Profile
import com.example.urbanhop.screens.ProfileScreen
import com.example.urbanhop.ui.theme.UrbanHopTheme
import com.kyant.backdrop.backdrops.rememberLayerBackdrop


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint(
        "UnusedMaterial3ScaffoldPaddingParameter",
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UrbanHopTheme {
                val backStack = rememberNavBackStack(PageMarker.Home.navKey)
                val contentPadding = WindowInsets.systemBars.asPaddingValues()
                var uiSize by remember { mutableStateOf(IntSize.Zero) }
                val backdrop = rememberLayerBackdrop()
                val textFieldState = rememberTextFieldState()
                val context = LocalContext.current


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    NavDisplay(
                        backStack = backStack,
                        entryProvider = { key ->
                            when (key) {
                                MapScreen -> {
                                    NavEntry(key) { Map(backdrop, uiSize) }
                                }
                                ARScreen -> {
                                    NavEntry(key) { AR() }
                                }
                                ProfileScreen -> {
                                    NavEntry(key) { Profile() }
                                }
                                else -> throw RuntimeException("Unknown key: $key")
                            }
                        }
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun Preview() {
    UrbanHopTheme {

    }
}

fun onSearch(search: String) {

}