package com.example.urbanhop.navigation.navbar

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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.draw.drawCrescent
import com.example.urbanhop.navigation.PageMarker
import com.example.urbanhop.utils.lightBlur
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import kotlinx.coroutines.launch

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    backstack: NavBackStack<NavKey>,
    backdrop: Backdrop,
    getSize: (IntSize) -> Unit,
    onSwiped: (PageMarker) -> Unit
) {
    val currentPage = PageMarker.entries.find { it.navKey == backstack.last() }
    val pageCount = PageMarker.entries.size
    val pagerState = rememberPagerState(
        initialPage = PageMarker.entries.indexOf(currentPage)
    ) { pageCount }
    val scope = rememberCoroutineScope()

    if (
        when (currentPage) {
            PageMarker.Ar,
            PageMarker.Home,
            PageMarker.Account -> true

            else -> false
        }
    ) {
        LaunchedEffect(pagerState.currentPage) {
            onSwiped(PageMarker.entries[pagerState.currentPage])
        }

        LaunchedEffect(backstack.last()) {
            if (pagerState.currentPage != PageMarker.entries.indexOf(currentPage)) {
                scope.launch {
                    pagerState.animateScrollToPage(PageMarker.entries.indexOf(currentPage))
                }
            }
        }
    }

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .clip(
                RoundedCornerShape(32.dp)
            )
            .onSizeChanged { size ->
                getSize(size)
            },
        windowInsets = WindowInsets()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(32.dp) },
                    effects = {
                        lightBlur()
                    },
                    onDrawSurface = {
                        drawRect(Color.White.copy(alpha = 0.20f))
                        navBarIndicator(pagerState)
                    },
                ),
            contentAlignment = Alignment.Center
        ) {
            NavBarElement(pagerState)
        }
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