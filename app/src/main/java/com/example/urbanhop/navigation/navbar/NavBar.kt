package com.example.urbanhop.navigation.navbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.navigation.PageMarker
import com.example.urbanhop.utils.lightBlur
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    backstack: NavBackStack<NavKey>,
    backdrop: Backdrop,
    getSize: (IntSize) -> Unit,
    onSelected: (PageMarker) -> Unit
) {
    val tabs = listOf(PageMarker.Ar, PageMarker.Home, PageMarker.Account)

    val currentKey = backstack.lastOrNull()
    val selectedIndex = tabs.indexOfFirst { it.navKey == currentKey }.let { idx ->
        if (idx == -1) 1 else idx // default Home
    }

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(32.dp))
            .onSizeChanged(getSize),
        windowInsets = WindowInsets()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedCornerShape(32.dp) },
                    effects = { lightBlur() },
                    onDrawSurface = {
                        drawRect(Color.White.copy(alpha = 0.20f))
                    },
                ),
            contentAlignment = Alignment.Center
        ) {
            NavBarIconsRow(
                tabs = tabs,
                selectedIndex = selectedIndex,
                onSelected = onSelected
            )
        }
    }
}

@Composable
private fun NavBarIconsRow(
    tabs: List<PageMarker>,
    selectedIndex: Int,
    onSelected: (PageMarker) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, marker ->
            val selected = index == selectedIndex

            val iconAlpha by animateFloatAsState(
                targetValue = if (selected) 1f else 0.75f,
                label = "iconAlpha"
            )
            val bgAlpha by animateFloatAsState(
                targetValue = if (selected) 0.50f else 0f,
                label = "bgAlpha"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = bgAlpha))
                    .border(
                        BorderStroke(
                            width = 2.dp,
                            color = Color.White.copy(alpha = bgAlpha)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clickable { onSelected(marker) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(marker.icon),
                    contentDescription = stringResource(marker.title),
                    tint = Color.White.copy(alpha = iconAlpha),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}