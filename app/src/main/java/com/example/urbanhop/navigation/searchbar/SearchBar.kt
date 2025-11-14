package com.example.urbanhop.navigation.searchbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.urbanhop.R
import com.example.urbanhop.onSearch
import com.example.urbanhop.utils.lightBlur
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchBarCustom(
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
                        "Ready to go?",
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
