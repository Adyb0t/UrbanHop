package com.example.urbanhop.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.R
import com.example.urbanhop.data.navigation_path.NavigationPath
import com.example.urbanhop.state.TrainNavScreenViewState
import com.example.urbanhop.state.TrainNavViewModel
import com.example.urbanhop.utils.filterAndRank
import com.example.urbanhop.utils.interchangePathNav
import com.example.urbanhop.utils.sharedLineNav
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data class TrainNavScreen(val selected: String) : NavKey

@Composable
fun TrainNav(
    viewModel: TrainNavViewModel = koinViewModel()
) {
    val trainNavViewState by viewModel.trainNavScreenViewState.collectAsStateWithLifecycle()
    val destinationTextField = rememberTextFieldState()
    val startingTextField = rememberTextFieldState()

    when (trainNavViewState) {
        is TrainNavScreenViewState.Loading -> {}

        is TrainNavScreenViewState.DirectionQuery -> {
            val (selected, stations, stationsRef) = (trainNavViewState as TrainNavScreenViewState.DirectionQuery)
            val isClicked = remember { mutableStateOf(false) }
            val isEmpty =
                destinationTextField.text.isEmpty() || startingTextField.text.isEmpty()
            val isFilled =
                destinationTextField.text.isNotEmpty() && startingTextField.text.isNotEmpty()
            val isValid =
                stationsRef.contains(destinationTextField.text.toString()) && stationsRef.contains(
                    startingTextField.text.toString()
                )

            LaunchedEffect(selected) {
                destinationTextField.edit { replace(0, length, selected) }
            }

            LaunchedEffect(isClicked.value) {
                delay(3000)
                isClicked.value = false
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Destination",
                    color = Color.White
                )
                CustomTextField(
                    textFieldState = destinationTextField,
                    stations = stationsRef.keys,
                    onValueChange = {
                        destinationTextField.edit { replace(0, length, it) }
                    },
                    icon = {
                        Icon(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.search_icon),
                            contentDescription = "Search",
                            tint = Color.Black
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Starting station",
                    color = Color.White
                )
                CustomTextField(
                    textFieldState = startingTextField,
                    stations = stationsRef.keys,
                    onValueChange = {
                        startingTextField.edit { replace(0, length, it) }
                    },
                    icon = {
                        Icon(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.search_icon),
                            contentDescription = "Search",
                            tint = Color.Black
                        )
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                ) {
                    Button(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.CenterEnd),
                        onClick = {
                            isClicked.value = true
                            if (isValid) viewModel.onConfirmStation()
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.DarkGray
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.rounded_close_24),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterStart),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        AnimatedVisibility(
                            visible = isEmpty && isClicked.value
                        ) {
                            Text(
                                text = "Please fill in the field",
                                color = Color.White,
                            )
                        }
                        AnimatedVisibility(
                            visible = isClicked.value && isFilled && !isValid
                        ) {
                            Text(
                                text = "Station not found",
                                color = Color.White,
                            )
                        }
                    }
                }
            }

        }

        is TrainNavScreenViewState.NavigationInfo -> {
            val (stations, lines, stationsRef) = (trainNavViewState as TrainNavScreenViewState.NavigationInfo)
            val sharedLines = mutableListOf<String>()
            val navigationPaths = mutableListOf<NavigationPath>()

            stationsRef[startingTextField.text.toString()]?.navigationLines?.forEach { startingLine ->
                stationsRef[destinationTextField.text.toString()]?.navigationLines?.forEach { destinationLine ->
                    if (startingLine.code == destinationLine.code) {
                        sharedLines.add(startingLine.code)
                    }
                }
            }

            Log.d("TrainNavScreen", sharedLines.toString())

            if (sharedLines.isNotEmpty()) {
                navigationPaths.add(
                    NavigationPath(
                        startLine = sharedLineNav(
                            lines.find { it.code == sharedLines[0] }!!,
                            stationsRef[startingTextField.text.toString()]!!,
                            stationsRef[destinationTextField.text.toString()]!!
                        ),
                        lines = listOf(sharedLines[0])
                    )
                )
            } else {
                navigationPaths.addAll(
                    interchangePathNav(
                        stationsRef[startingTextField.text.toString()]!!,
                        stationsRef[destinationTextField.text.toString()]!!,
                    )
                )
            }

            Log.d("TrainNavScreen", navigationPaths.toString())

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
//                        .background(Color.Black)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = navigationPaths[0].lines[0],
                        color = Color.Black
                    )
                    navigationPaths[0].startLine.forEach {
                        Text(
                            text = it,
                            color = Color.Red
                        )
                    }
                    if (navigationPaths[0].interchangeLine.isNotEmpty()) {
                        Text(
                            text = navigationPaths[0].lines[1],
                            color = Color.Black
                        )
                    }
                    navigationPaths[0].interchangeLine.forEach {
                        Text(
                            text = it,
                            color = Color.Blue
                        )
                    }
//                    Text(startingTextField.text.toString())
//                    Text(destinationTextField.text.toString())
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    textFieldState: TextFieldState,
    stations: Set<String>,
    onValueChange: (String) -> Unit = {},
    icon: @Composable BoxScope.() -> Unit = {}
) {
    val filteredAndRanked = filterAndRank(stations, textFieldState.text.toString())

    Column {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            value = textFieldState.text.toString(),
            onValueChange = {
                onValueChange(it)
            },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    icon()
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
            ),
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(
            visible = filteredAndRanked.isNotEmpty() && textFieldState.text.toString().length > 1 && textFieldState.text.toString() != filteredAndRanked.first()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray),
            ) {
                items(filteredAndRanked) { suggestion ->
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                onClick = {
                                    onValueChange(suggestion)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        text = suggestion,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}