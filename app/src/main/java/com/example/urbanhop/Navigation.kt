package com.example.urbanhop

import android.annotation.SuppressLint
import android.graphics.RuntimeShader
import android.graphics.RenderEffect
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanhop.graphics.Backdrop
import com.example.urbanhop.graphics.backdrops.LayerBackdrop
import com.example.urbanhop.graphics.backdrops.rememberLayerBackdrop
import com.example.urbanhop.graphics.drawBackdrop
import com.example.urbanhop.graphics.effects.blur
import com.example.urbanhop.graphics.effects.lens
import com.example.urbanhop.graphics.effects.vibrancy
import com.example.urbanhop.graphics.greyscaleEffect
import com.example.urbanhop.graphics.highlight.Highlight

enum class PageMarker(
    val title: Int,
    val icon: Int
) {
    Ar(
        R.string.ar_string,
        R.drawable.ar_icon
    ),
    Home( //Home has to be in the middle
        R.string.home_string,
        R.drawable.home_icon
    ),
    Account(
        R.string.account_string,
        R.drawable.account_icon
    )
}

@Composable
fun NavBar(
    paddingUI: PaddingValues,
    selectedMarkerType: PageMarker,
    backdrop: Backdrop,
    onMarkerClicked: (PageMarker) -> Unit
) {

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 8.dp)
            .clip(
                RoundedCornerShape(32.dp)
            ),
        windowInsets = WindowInsets()
    ) {
        Box(
            modifier = Modifier
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(32.dp) },
                    effects = {
                        vibrancy()
                        blur(10f.dp.toPx())
                        lens(
                            16f.dp.toPx(),
                            32f.dp.toPx(),
                        )
                    },
                    onDrawSurface = { drawRect(Color.Black.copy(alpha = 0.25f)) },
                )
                .fillMaxSize()
        ) {

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
//                .background(Color.White)
                .border(
                    BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                    RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 12.dp)
        ) {
            PageMarker.entries.forEachIndexed { i, marker ->
                val selected = selectedMarkerType == marker

                this@NavigationBar.NavigationBarItem(
                    modifier = Modifier
                        .weight(if (selected) 3f else 1f),
                    selected = selected,
                    onClick = {
                        onMarkerClicked(marker)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Black,
                        selectedIconColor = Color.White
                    ),
                    icon = {
                        if (!selected) {
                            Icon(
                                modifier = Modifier
                                    .size(48.dp),
                                painter = painterResource(marker.icon),
                                contentDescription = stringResource(marker.title),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(
                                            RoundedCornerShape(
                                                24.dp
                                            )
                                        )
                                        .background(Color.Black.copy(0.75f))
//                                        .border(
//                                            BorderStroke(1.dp, Color.White.copy(alpha = 1f)),
//                                            RoundedCornerShape(24.dp)
//                                        )
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (i < 1) {
                                        Icon(
                                            modifier = Modifier
                                                .size(36.dp),
                                            painter = painterResource(marker.icon),
                                            contentDescription = stringResource(
                                                marker.title
                                            ),
                                        )
                                        Spacer(
                                            modifier = Modifier
                                                .width(2.dp)
                                        )
                                        Text(
                                            text = stringResource(marker.title),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    } else if (i > 1) {
                                        Text(
                                            text = stringResource(marker.title),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(
                                            modifier = Modifier
                                                .width(2.dp)
                                        )
                                        Icon(
                                            modifier = Modifier
                                                .size(36.dp),
                                            painter = painterResource(marker.icon),
                                            contentDescription = stringResource(
                                                marker.title
                                            ),
                                        )
                                    } else {
                                        Icon(
                                            modifier = Modifier
                                                .size(36.dp),
                                            painter = painterResource(marker.icon),
                                            contentDescription = stringResource(
                                                marker.title
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}