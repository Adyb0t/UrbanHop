package com.example.urbanhop.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.R
import com.example.urbanhop.data.navigation_path.NavigationPath
import com.example.urbanhop.data.navigation_stations.NavigationLine
import com.example.urbanhop.state.TrainNavScreenViewState
import com.example.urbanhop.state.TrainNavViewModel
import com.example.urbanhop.ui.theme.LocalLineColors
import com.example.urbanhop.utils.ViewportSize
import com.example.urbanhop.utils.filterAndRank
import com.example.urbanhop.utils.interchangePathNav
import com.example.urbanhop.utils.rememberViewportSize
import com.example.urbanhop.utils.sharedLineNav
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    BackHandler(trainNavViewState is TrainNavScreenViewState.NavigationInfo) {
        viewModel.onBack()
    }

    when (trainNavViewState) {
        is TrainNavScreenViewState.Loading -> {}

        is TrainNavScreenViewState.DirectionQuery -> {
            val (selected, _, stationsRef) = (trainNavViewState as TrainNavScreenViewState.DirectionQuery)
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
                    .background(colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    isStarting = true,
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
                            painter = painterResource(id = R.drawable.outline_train),
                            contentDescription = "Search",
                            tint = colorScheme.onSecondary
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Starting station",
                            color = colorScheme.onSecondary.copy(0.5f)
                        )
                    }
                )
                CustomTextField(
                    isStarting = false,
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
                            painter = painterResource(id = R.drawable.outline_train),
                            contentDescription = "Search",
                            tint = colorScheme.onSecondary
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Enter destination",
                            color = colorScheme.onSecondary.copy(0.5f)
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
                            containerColor = colorScheme.secondary,
                            contentColor = colorScheme.onSecondary,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.DarkGray
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_right_icon),
                            contentDescription = null,
                            tint = colorScheme.onSecondary
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
                                color = colorScheme.secondary,
                            )
                        }
                        AnimatedVisibility(
                            visible = isClicked.value && isFilled && !isValid
                        ) {
                            Text(
                                text = "Station not found",
                                color = colorScheme.secondary,
                            )
                        }
                    }
                }
            }

        }

        is TrainNavScreenViewState.NavigationInfo -> {
            val (_, lines, stationsRef) = (trainNavViewState as TrainNavScreenViewState.NavigationInfo)
            val (viewport, viewportModifier) = rememberViewportSize()
            val startViewport = viewport ?: ViewportSize(IntSize(0, 0), 0.dp, 0.dp)
            val interViewport = viewport ?: ViewportSize(IntSize(0, 0), 0.dp, 0.dp)
            val sharedLines = mutableListOf<String>()
            val navigationPaths = mutableListOf<NavigationPath>()
            var isOpenedStart by remember { mutableStateOf(false) }
            var isOpenedInter by remember { mutableStateOf(false) }
            var multiplierStart by remember { mutableIntStateOf(1) }
            var multiplierInter by remember { mutableIntStateOf(1) }


            stationsRef[startingTextField.text.toString()]?.navigationLines?.forEach { startingLine ->
                stationsRef[destinationTextField.text.toString()]?.navigationLines?.forEach { destinationLine ->
                    if (startingLine.code == destinationLine.code) {
                        sharedLines.add(startingLine.code)
                    }
                }
            }

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

            if (navigationPaths.isNotEmpty()) {
                multiplierStart = ((navigationPaths.first().startLine.size - 1) / 15) + 1
            }
            if (navigationPaths.isNotEmpty()) {
                multiplierInter = ((navigationPaths.first().interchangeLine.size - 1) / 15) + 1
            }

            val viewportStartScale by animateDpAsState(
                targetValue =
                    if (isOpenedStart) {
                        (startViewport.height.times(multiplierStart))
                    } else startViewport.height,
            )
            val viewportInterScale by animateDpAsState(
                targetValue =
                    if (isOpenedInter) {
                        (interViewport.height.times(multiplierInter))
                    } else interViewport.height,
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(viewportModifier)
                    .background(colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                NavUI(
                    viewportStartScale - if (navigationPaths.first().interchangeLine.isNotEmpty()) 56.dp else 0.dp,
                    isOpenedStart,
                    startViewport,
                    navigationPaths.first().lines.first(),
                    navigationPaths.first().startLine,
                    lines,
                ) {
                    isOpenedStart = it
                }
                AnimatedVisibility(
                    visible = navigationPaths.first().interchangeLine.isNotEmpty()
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        StaticRouteUI(
                            colorScheme.primary,
                            64.dp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .background(colorScheme.primary),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Interchange to ",
                                color = colorScheme.onPrimary,
                                fontSize = 20.sp
                            )
                            Text(
                                text = lines.find { it.code == navigationPaths.first().lines[1] }?.name!!,
                                color = LocalLineColors.current.find {
                                    it.code == navigationPaths.first().lines[1]
                                }?.color!!,
                                fontSize = 24.sp
                            )

                        }
                    }
                }
                AnimatedVisibility(
                    visible = navigationPaths.first().interchangeLine.isNotEmpty()
                ) {
                    NavUI(
                        viewportInterScale - 56.dp,
                        isOpenedInter,
                        startViewport,
                        navigationPaths.first().lines[1],
                        navigationPaths.first().interchangeLine,
                        lines,
                    ) {
                        isOpenedInter = it
                    }
                }
            }
        }
    }
}

@Composable
private fun NavUI(
    viewportScale: Dp,
    isOpened: Boolean,
    viewport: ViewportSize?,
    code: String,
    path: List<String>,
    lines: List<NavigationLine>,
    onClick: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(viewportScale)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var topRowTextSizePx by remember { mutableStateOf(IntSize.Zero) }
            var bottomRowTextSizePx by remember { mutableStateOf(IntSize.Zero) }
            var expandButtonSizePx by remember { mutableStateOf(IntSize.Zero) }
            val rotation by animateFloatAsState(
                targetValue = if (isOpened) 270f else 90f,
            )
            val density = LocalDensity.current
            val translation by animateFloatAsState(
                targetValue = if (isOpened) 1f else 0f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
            Row(
                modifier = Modifier
                    .onSizeChanged { topRowTextSizePx = it }
                    .offset(
                        x = run {
                            val viewportDp = viewport?.width ?: 0.dp
                            val rowWidthDp =
                                with(density) { topRowTextSizePx.width.toDp() }
                            -((viewportDp / 2) - (rowWidthDp / 2) - 32.dp) * translation
                        }
                    )
            ) {
                Text(
                    text = path.first(),
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = run {
                                val viewportDp = viewport?.width ?: 0.dp
                                -((viewportDp / 2) - 64.dp) * translation
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    RouteUI(
                        line =
                            lines.find {
                                it.code == code
                            }?.name!!,
                        path = path,
                        isOpened = isOpened,
                        color =
                            LocalLineColors.current.find {
                                it.code == code
                            }?.color!!,
                    )
                }
                Row(
                    modifier = Modifier
                        .onSizeChanged { expandButtonSizePx = it }
                        .offset(
                            x = run {
                                val viewportDp = viewport?.width ?: 0.dp
                                val rowWidthDp =
                                    with(density) { expandButtonSizePx.width.toDp() }
                                ((viewportDp / 2) - (rowWidthDp / 2) - 16.dp)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = path.size > 2 && !isOpened
                    ) {
                        Text(
                            text = "${path.size - 2} stations",
                            fontSize = 16.sp,
                            color = colorScheme.onBackground
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    AnimatedVisibility(
                        visible = path.size > 2
                    ) {
                        Button(
                            modifier = Modifier
                                .size(40.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = colorScheme.onBackground,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.DarkGray
                            ),
                            onClick = {
                                onClick(!isOpened)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .rotate(rotation),
                                painter = painterResource(id = R.drawable.arrow_right_icon),
                                contentDescription = null,
                                tint = colorScheme.onBackground
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .onSizeChanged { bottomRowTextSizePx = it }
                    .offset(
                        x = run {
                            val viewportDp = viewport?.width ?: 0.dp
                            val rowWidthDp =
                                with(density) { bottomRowTextSizePx.width.toDp() }
                            -((viewportDp / 2) - (rowWidthDp / 2) - 32.dp) * translation
                        }
                    )
            ) {
                Text(
                    text = path.last(),
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp
                )
            }
        }
    }
}

@Composable
private fun RouteUI(
    line: String,
    path: List<String>,
    isOpened: Boolean,
    color: Color,
) {
    val initScale = remember { Animatable(0.0f) }
    val initTranslation = remember { Animatable(0.0f) }
    val initTextTranslation = remember { Animatable(0.dp, Dp.VectorConverter) }
    val initTextAlpha = remember { Animatable(0f) }
    val nodeAnim = remember { Animatable(0f) }
    val nodeAlpha = remember { Animatable(0f) }


    LaunchedEffect(Unit) {
        launch {
            initTranslation.animateTo(
                targetValue = 0.8f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
        }

        launch {
            initScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = 100,
                    easing = FastOutSlowInEasing
                )
            )
        }

        launch {
            initTextTranslation.animateTo(
                targetValue = (-32).dp,
                animationSpec = tween(
                    durationMillis = 250,
                    delayMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
        }

        launch {
            initTextAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 250,
                    delayMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    LaunchedEffect(isOpened) {
        launch {
            if (isOpened) {
                nodeAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = LinearEasing
                    )
                )
            } else {
                nodeAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }

        launch {
            if (isOpened) {
                nodeAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearEasing
                    )
                )
            } else {
                nodeAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxHeight(initTranslation.value),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .offset(x = initTextTranslation.value)
                .rotate(270f),
            text = line,
            color = colorScheme.onBackground.copy(initTextAlpha.value),
            fontSize = 24.sp
        )
        Column(
            modifier = Modifier
                .graphicsLayer {
                    scaleY = initScale.value
                }
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EndCircle(true, color)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .width(20.dp)
                    .background(color)
            )
            EndCircle(false, color)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = !isOpened,
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.background)
                            .size(width = 34.dp, height = 16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                            .size(width = 28.dp, height = 8.dp)
                    )
                }
            }
            for (i in 1..path.size - 2) {
                StationNode(
                    translationAnim = nodeAnim.value,
                    alphaAnim = nodeAlpha.value,
                    station = path[i],
                    color = color
                )
            }
        }
    }
}

@Composable
private fun StaticRouteUI(
    color: Color,
    height: Dp
) {
    val initScale = remember { Animatable(0.0f) }

    LaunchedEffect(Unit) {
        launch {
            initScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = 250,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .graphicsLayer {
                scaleY = initScale.value
            }
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EndCircle(true, color)
        Box(
            modifier = Modifier
                .height(height + 48.dp)
                .width(20.dp)
                .background(color)
        )
        EndCircle(false, color)
    }
}

@Composable
private fun ColumnScope.StationNode(
    translationAnim: Float,
    alphaAnim: Float,
    station: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = translationAnim
                alpha = alphaAnim
            }
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        var textSizePx by remember { mutableStateOf(IntSize.Zero) }
        val density = LocalDensity.current
        val textWidthDp = with(density) {
            textSizePx.width.toDp()
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.background)
                .size(width = 34.dp, height = 16.dp)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(color)
                .size(width = 28.dp, height = 8.dp)
        )
        Box(
            modifier = Modifier
                .onSizeChanged {
                    textSizePx = it
                }
                .heightIn(min = 24.dp)
                .offset(
                    x = textWidthDp / 2 + 32.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                text = station,
                color = colorScheme.onBackground,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun EndCircle(
    top: Boolean,
    color: Color
) {
    Box(
        modifier = Modifier
            .offset(y = if (top) 6.dp else (-6).dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(colorScheme.background)
            )
        }
    }
}

@Composable
private fun CustomTextField(
    isStarting: Boolean,
    textFieldState: TextFieldState,
    stations: Set<String>,
    onValueChange: (String) -> Unit = {},
    icon: @Composable BoxScope.() -> Unit = {},
    placeholder: @Composable () -> Unit
) {
    val filteredAndRanked = filterAndRank(stations, textFieldState.text.toString())

    if (filteredAndRanked.size == 1 && textFieldState.text.length >= filteredAndRanked.first().length) {
        onValueChange(filteredAndRanked.first())
    }

    val showSuggestion =
        filteredAndRanked.isNotEmpty() && textFieldState.text.toString().isNotEmpty() &&
                textFieldState.text.toString() != filteredAndRanked.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .height(64.dp)
        ) {
            if (!isStarting && showSuggestion) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.primary)
                )
            }
            TextField(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        if (isStarting) RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        else RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .background(colorScheme.secondary)
                    .topOrBottomIndicator(
                        colorScheme.background,
                        isBottom = isStarting
                    ),
                value = textFieldState.text.toString(),
                onValueChange = {
                    onValueChange(it)
                },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                    ) {
                        icon()
                    }
                },
                placeholder = {
                    placeholder()
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
        }
        AnimatedVisibility(
            visible = showSuggestion
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        if (!isStarting) RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                        else RoundedCornerShape(0.dp)
                    )
                    .background(colorScheme.primary),
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
                        color = colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun Modifier.topOrBottomIndicator(
    color: Color,
    isBottom: Boolean,
    thickness: Dp = 2.dp
) = drawBehind {
    val y = if (isBottom) size.height else 0f

    drawLine(
        color = color,
        start = Offset(size.width * 0.1f, y),
        end = Offset(size.width * 0.9f, y),
        strokeWidth = thickness.toPx()
    )
}