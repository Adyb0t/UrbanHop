package com.example.urbanhop

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.draw.drawCrescent
import com.example.urbanhop.screens.ARScreen
import com.example.urbanhop.screens.MapScreen
import com.example.urbanhop.screens.ProfileScreen
import com.example.urbanhop.utils.lightBlur
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import kotlinx.coroutines.launch

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

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NavigationUI(
    textFieldState: TextFieldState,
    backdrop: LayerBackdrop,
    backstack: NavBackStack<NavKey>,
    getComponentSize: (IntSize) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        SearchBarBackdrop(
            Modifier.align(Alignment.TopCenter),
            textFieldState,
            backdrop,
            keyboardController
        )
        NavBar(
            Modifier.align(Alignment.BottomCenter),
            backstack,
            backdrop,
            getComponentSize
        ) {
            if (backstack.last() != it.navKey) backstack.add(it.navKey)
            if (it.navKey == PageMarker.Home.navKey) {
                backstack.clear()
                backstack.add(PageMarker.Home.navKey)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchBarBackdrop(
    modifier: Modifier,
    textFieldState: TextFieldState,
    backdrop: LayerBackdrop,
    keyboardController: SoftwareKeyboardController?
) {
    var expandedSearch by remember { mutableStateOf(false) }

    SearchBar(
        modifier = modifier
            .fillMaxWidth(),
        colors = SearchBarDefaults.colors(
            containerColor = if (expandedSearch) Color.Black else Color.Transparent,
            dividerColor = Color.Transparent
        ),
        inputField = {
            TextField(
                value = textFieldState.text.toString(),
                onValueChange = {
                    textFieldState.edit { replace(0, length, it) }
                    expandedSearch = it.isNotEmpty()
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedCornerShape(32.dp) },
                        effects = {
                            lightBlur()
                        },
                        onDrawSurface = {
                            if (!expandedSearch) drawRect(Color.White.copy(alpha = 0.20f))
                            else drawRect(Color.White.copy(alpha = 0.50f))
                        },
                    )
                    .padding(horizontal = 8.dp),
                placeholder = {
                    Text(
                        "Want to explore?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                onClick = {
                                    onSearch(textFieldState.text.toString())
                                    keyboardController?.hide()
                                    expandedSearch = false
                                }
                            ),
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = "Search"
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(fontSize = 16.sp),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        keyboardController?.hide()
                        expandedSearch = false
                    }
                ),
                singleLine = true
            )
        },
        expanded = expandedSearch,
        onExpandedChange = { expandedSearch = it },
    ) {
        ExpandedSearch()
    }
}

@Composable
private fun ExpandedSearch() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(Color.White)
    )
}

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