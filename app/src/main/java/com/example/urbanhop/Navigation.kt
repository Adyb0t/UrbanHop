package com.example.urbanhop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanhop.draw.drawCrescent
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import kotlinx.coroutines.launch

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
    modifier: Modifier = Modifier,
    selectedMarkerType: PageMarker,
    backdrop: Backdrop,
    onMarkerSwiped: (PageMarker) -> Unit
) {
    val pageCount = PageMarker.entries.size
    val pagerState = rememberPagerState(
        initialPage = PageMarker.entries.indexOf(selectedMarkerType)
    ) { pageCount }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onMarkerSwiped(PageMarker.entries[pagerState.currentPage])
    }

    LaunchedEffect(selectedMarkerType) {
        if (pagerState.currentPage != PageMarker.entries.indexOf(selectedMarkerType)) {
            scope.launch {
                pagerState.animateScrollToPage(PageMarker.entries.indexOf(selectedMarkerType))
            }
        }
    }

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 64.dp)
            .clip(
                RoundedCornerShape(32.dp)
            ),
        windowInsets = WindowInsets()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(32.dp) },
                    effects = {
                        vibrancy()
                        blur(5f.dp.toPx())
                        lens(
                            16f.dp.toPx(),
                            32f.dp.toPx(),
                        )
                    },
                    onDrawSurface = {
                        drawRect(Color.Black.copy(alpha = 0.50f))
                        navBarIndicator(pagerState)
                    },
                ),
            contentAlignment = Alignment.Center
        ) {
            NavBarElement(pagerState)
        }

//        Row(
//            modifier = Modifier
//                .drawBackdrop(
//                    backdrop = backdrop,
//                    shape = { RoundedCornerShape(32.dp) },
//                    effects = {
//                        vibrancy()
//                        blur(5f.dp.toPx())
//                        lens(
//                            16f.dp.toPx(),
//                            32f.dp.toPx(),
//                        )
//                    },
//                    onDrawSurface = { drawRect(Color.Black.copy(alpha = 0.50f)) },
//                )
//                .fillMaxWidth()
//                .padding(horizontal = 12.dp)
//        ) {
//            PageMarker.entries.forEachIndexed { i, marker ->
//                val selected = selectedMarkerType == marker
//
//                this@NavigationBar.NavigationBarItem(
//                    modifier = Modifier
//                        .weight(if (selected) 3f else 1f),
//                    selected = selected,
//                    onClick = {
//                        onMarkerClicked(marker)
//                    },
//                    colors = NavigationBarItemDefaults.colors(
//                        indicatorColor = Color.Transparent,
//                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
//                        selectedIconColor = Color.Black
//                    ),
//                    icon = {
//                        if (!selected) {
//                            Icon(
//                                modifier = Modifier
//                                    .size(48.dp),
//                                painter = painterResource(marker.icon),
//                                contentDescription = stringResource(marker.title),
//                            )
//                        } else {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth(),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .clip(
//                                            RoundedCornerShape(
//                                                24.dp
//                                            )
//                                        )
//                                        .background(Color.White.copy(alpha = 0.5f))
//                                        .padding(vertical = 8.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.Center
//                                ) {
//                                    if (i < 1) {
//                                        Icon(
//                                            modifier = Modifier
//                                                .size(40.dp),
//                                            painter = painterResource(marker.icon),
//                                            contentDescription = stringResource(
//                                                marker.title
//                                            ),
//                                        )
//                                        Spacer(
//                                            modifier = Modifier
//                                                .width(2.dp)
//                                        )
//                                        Text(
//                                            text = stringResource(marker.title),
//                                            fontSize = 24.sp,
//                                            fontWeight = FontWeight.Bold,
//                                        )
//                                    } else if (i > 1) {
//                                        Text(
//                                            text = stringResource(marker.title),
//                                            fontSize = 24.sp,
//                                            fontWeight = FontWeight.Bold,
//                                        )
//                                        Spacer(
//                                            modifier = Modifier
//                                                .width(2.dp)
//                                        )
//                                        Icon(
//                                            modifier = Modifier
//                                                .size(40.dp),
//                                            painter = painterResource(marker.icon),
//                                            contentDescription = stringResource(
//                                                marker.title
//                                            ),
//                                        )
//                                    } else {
//                                        Icon(
//                                            modifier = Modifier
//                                                .size(40.dp),
//                                            painter = painterResource(marker.icon),
//                                            contentDescription = stringResource(
//                                                marker.title
//                                            ),
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    },
//                )
//            }
//        }
    }
}

@Composable
private fun NavBarElement(pagerState: PagerState) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val marker = PageMarker.entries[page]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (page < 1) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(marker.icon),
                    contentDescription = stringResource(marker.title),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = stringResource(marker.title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            } else if (page > 1) {
                Text(
                    text = stringResource(marker.title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(marker.icon),
                    contentDescription = stringResource(marker.title),
                    tint = Color.White
                )
            } else {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(marker.icon),
                    contentDescription = stringResource(marker.title),
                    tint = Color.White
                )
            }
        }
    }
}

private fun DrawScope.navBarIndicator(pagerState: PagerState) {
    val crescentRadius = 22.dp.toPx()
    val crescentOffset = 4.dp.toPx()

    when (pagerState.currentPage) {
        0 -> {
            drawCrescent(
                xPosition = size.width - crescentRadius - 16,
                flipped = true,
                crescentOffset = crescentOffset,
                crescentRadius = crescentRadius
            )
        }

        2 -> {
            drawCrescent(
                xPosition = crescentRadius + 16,
                flipped = false,
                crescentOffset = crescentOffset,
                crescentRadius = crescentRadius
            )
        }

        else -> {
            drawCrescent(
                xPosition = crescentRadius + 16,
                flipped = false,
                crescentOffset = crescentOffset,
                crescentRadius = crescentRadius
            )
            drawCrescent(
                xPosition = size.width - crescentRadius - 16,
                flipped = true,
                crescentOffset = crescentOffset,
                crescentRadius = crescentRadius
            )
        }
    }
}