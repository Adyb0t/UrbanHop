package com.example.urbanhop

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.urbanhop.graphics.backdrops.LayerBackdrop
import com.example.urbanhop.graphics.backdrops.layerBackdrop
import com.example.urbanhop.graphics.backdrops.rememberLayerBackdrop
import com.example.urbanhop.pages.Map
import com.example.urbanhop.ui.theme.UrbanHopTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint(
        "UnusedMaterial3ScaffoldPaddingParameter",
        "UnusedContentLambdaTargetStateParameter"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UrbanHopTheme {
                var selectedPage by rememberSaveable {
                    mutableStateOf(PageMarker.Home)
                }
                val contentPadding = WindowInsets.systemBars.asPaddingValues()
                val backdrop = rememberLayerBackdrop()

                Box(
                    modifier = Modifier
                        .layerBackdrop(backdrop)
                        .fillMaxSize()
                ) {

                    NavBar(
                            contentPadding,
                            selectedPage,
                            backdrop
                        ) { selectedPage = it }
                }

//                Scaffold(
//                    containerColor = Color.Transparent,
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    bottomBar = {
//                        NavBar(
//                            contentPadding,
//                            selectedPage,
//                            backdrop
//                        ) { selectedPage = it }
//                    }
//                ) {
//                    Map(contentPadding, backdrop)
//                }

//                val isLightTheme = !isSystemInDarkTheme()
//                val contentColor = if (isLightTheme) Color.Black else Color.White
//
//                val airplaneModeIcon = painterResource(R.drawable.flight_40px)
//                val iconColorFilter = ColorFilter.tint(contentColor)
//
//                BackdropDemoScaffold(
//                    initialPainterResId = R.drawable.system_home_screen_light
//                ) { backdrop ->
//                    Column(verticalArrangement = Arrangement.spacedBy(32f.dp)) {
//                        Block {
//                            var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
//
//                            LiquidBottomTabs(
//                                selectedTabIndex = { selectedTabIndex },
//                                onTabSelected = { selectedTabIndex = it },
//                                backdrop = backdrop,
//                                tabsCount = 3,
//                                modifier = Modifier.padding(horizontal = 36f.dp)
//                            ) {
//                                repeat(3) { index ->
//                                    LiquidBottomTab({ selectedTabIndex = index }) {
//                                        Box(
//                                            Modifier
//                                                .size(28f.dp)
//                                                .paint(airplaneModeIcon, colorFilter = iconColorFilter)
//                                        )
//                                        BasicText(
//                                            "Tab ${index + 1}",
//                                            style = TextStyle(contentColor, 12f.sp)
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
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

//@Composable
//fun BackdropScaffold(
//    modifier: Modifier,
//    content: @Composable BoxScope.(backdrop: LayerBackdrop) -> Unit
//) {
//    Box(
//        modifier = Modifier
//    ) {
//        val backdrop = rememberLayerBackdrop()
//        Image(
//            painterResource(R.drawable.wallpaper_light),
//            null,
//            Modifier
//                .layerBackdrop(backdrop)
//                .then(modifier)
//                .fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
////        Map(PaddingValues(10.dp), backdrop)
//        content(backdrop)
//    }
//}