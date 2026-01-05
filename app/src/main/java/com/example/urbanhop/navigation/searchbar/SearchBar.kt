package com.example.urbanhop.navigation.searchbar

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanhop.R
import com.example.urbanhop.data.navigation_stations.NavigationStation
import com.example.urbanhop.utils.filterAndRank
import com.example.urbanhop.utils.lightBlur
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop

const val CANDIDATE_COUNT = 20
const val SUGGESTION_COUNT = 5

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarCustom(
    modifier: Modifier,
    textFieldState: TextFieldState,
    navigationStations: List<NavigationStation>,
    backdrop: LayerBackdrop,
    keyboardController: SoftwareKeyboardController?,
    onSearch: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit
) {
    var expandedSearch by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val stationsRef = remember(navigationStations) {
        mutableListOf<String>().apply {
            navigationStations.forEach { station ->
                station.names.forEach { add(it) }
            }
        }
    }
    val filteredAndRanked = filterAndRank(stationsRef, textFieldState.text.toString())
    val searchBackground by animateFloatAsState(
        targetValue = if (expandedSearch) 1f else 0.0f
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxSize(searchBackground)
                .background(colorScheme.background)
        )
        SearchBar(
            modifier = modifier
                .fillMaxWidth(),
            colors = SearchBarDefaults.colors(
                containerColor = Color.Transparent,
                dividerColor = Color.Transparent
            ),
            inputField = {
                TextField(
                    value = textFieldState.text.toString(),
                    onValueChange = {
                        textFieldState.edit { replace(0, length, it) }
                        expandedSearch = it.isNotEmpty()
                        onExpandedChange(expandedSearch)
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            expandedSearch = (it.isFocused && !expandedSearch)
                            onExpandedChange(expandedSearch)
                        }
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
                            "Ready to go?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    },
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable(
                                    onClick = {
                                        if (textFieldState.text.toString().length > 2) {
                                            expandedSearch = onCollapseSearch(
                                                textFieldState.text.toString(),
                                                filteredAndRanked,
                                                onSearch,
                                                keyboardController,
                                                true
                                            )
                                        }
                                    }
                                )
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(id = R.drawable.search_icon),
                                contentDescription = "Search",
                                tint = colorScheme.tertiary
                            )
                        }
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
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            expandedSearch = onCollapseSearch(
                                textFieldState.text.toString(),
                                filteredAndRanked,
                                onSearch,
                                keyboardController,
                                expandedSearch
                            )
                        }
                    ),
                    singleLine = true
                )
            },
            expanded = expandedSearch,
            onExpandedChange = { focusManager.clearFocus() },
        ) {
            ExpandedSearch(
                filteredAndRanked,
                textFieldState.text.toString()
            ) { selected ->
                onSearch(selected)
            }
        }
    }
}

@Composable
private fun ExpandedSearch(
    filteredAndRanked: List<String>,
    query: String,
    onSearch: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        SearchRow(
            text = query,
            icon = {
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Your Query",
                    tint = colorScheme.surface
                )
            },
            onClickRow = { _ ->
                onCollapseSearch(
                    query = query,
                    filteredAndRanked = filteredAndRanked,
                    onSearch = onSearch
                )
            }
        )
        filteredAndRanked.forEach { name ->
            SearchRow(
                text = name,
                icon = {
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.outline_train),
                        contentDescription = "Station",
                        tint = colorScheme.surface
                    )
                },
                onClickRow = { selected ->
                    onSearch(selected)
                }
            )
        }
    }
}

private fun onCollapseSearch(
    query: String,
    filteredAndRanked: List<String>,
    onSearch: (String) -> Unit,
    keyboardController: SoftwareKeyboardController? = null,
    expandedSearch: Boolean = false
): Boolean {
    if (query.length > 2 && filteredAndRanked.isNotEmpty()) {
        onSearch(filteredAndRanked.first())
    } else {
        Log.d("SearchBarCustom", "No search results")
    }
    keyboardController?.hide()
    return !expandedSearch
}

@Composable
private fun SearchRow(
    text: String,
    icon: @Composable BoxScope.() -> Unit,
    onClickRow: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { onClickRow(text) }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(colorScheme.tertiary)
                ) {
                    icon()
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    color = colorScheme.tertiary,
                    fontSize = 20.sp
                )
            }
            Row {
                Spacer(modifier = Modifier.width(44.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp)
                        .height(1.dp)
                        .background(colorScheme.tertiary)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
