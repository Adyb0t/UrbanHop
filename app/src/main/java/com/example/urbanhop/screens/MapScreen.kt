package com.example.urbanhop.screens

import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.urbanhop.R
import com.example.urbanhop.data.event_stations.EventStation
import com.example.urbanhop.data.events.Coordinate
import com.example.urbanhop.data.events.Event
import com.example.urbanhop.data.navigation_stations.NavigationStation
import com.example.urbanhop.draw.CustomMarker
import com.example.urbanhop.navigation.searchbar.SearchBarCustom
import com.example.urbanhop.state.MapScreenViewState
import com.example.urbanhop.state.MapViewModel
import com.example.urbanhop.utils.mapToMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
data object MapScreen : NavKey

private const val PIN_OFFSET = -80

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Map(
    resetPage: Boolean,
    backdrop: LayerBackdrop,
    backStack: NavBackStack<NavKey>,
    uiSize: IntSize = IntSize.Zero,
    onHomeSelectedCallback: () -> Unit,
    viewModel: MapViewModel = koinViewModel()
) {
    val mapViewState by viewModel.mapScreenViewState.collectAsStateWithLifecycle()
    var navStations: List<NavigationStation> by remember { mutableStateOf(emptyList()) }
    var isZoomedToBound by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var openedMarker: EventStation? by remember { mutableStateOf(null) }
    val cameraPositionState = rememberCameraPositionState {
        position =
            CameraPosition.fromLatLngZoom(
                LatLng(3.2510878785510826, 101.73429681730538),
                15f
            )
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val textFieldState = rememberTextFieldState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiHeight = with(LocalDensity.current) { uiSize.height.toDp() }

    LaunchedEffect(resetPage) {
        if (resetPage) {
            openedMarker = null
            viewModel.displayAllStation()
            onHomeSelectedCallback()
        }
    }

    BackHandler(mapViewState is MapScreenViewState.EventList) {
        viewModel.displayAllStation()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        GoogleMap(
            modifier = Modifier
                .layerBackdrop(backdrop)
                .fillMaxSize(),
            googleMapOptionsFactory = {
                GoogleMapOptions().mapId("614fcd75adf6485aba9fb036")
            },
            onMapLoaded = { isMapLoaded = true },
            cameraPositionState = cameraPositionState,
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM,
            contentPadding = PaddingValues(
                bottom = uiHeight + 8.dp,
                top = uiHeight + 8.dp
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false
            )
        )

        when (mapViewState) {
            is MapScreenViewState.LoadingPage -> {}

            is MapScreenViewState.LoadingEvents -> {
                openedMarker?.let { marker ->
                    val transition = rememberInfiniteTransition()
                    val radius by transition.animateFloat(
                        initialValue = 0f,
                        targetValue = 2000f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(750),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    val radiusSecond by transition.animateFloat(
                        initialValue = 0f,
                        targetValue = 2000f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(750),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(150)
                        )
                    )
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center =
                            marker.coordinates.mapToMap(cameraPositionState = cameraPositionState)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.3f),
                            radius = radius,
                            center = Offset(
                                center.x.toFloat(),
                                center.y.toFloat()
                            ),
                            style = Stroke(width = 4.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.1f),
                            radius = radius,
                            center = Offset(
                                center.x.toFloat(),
                                center.y.toFloat()
                            )
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.3f),
                            radius = radiusSecond,
                            center = Offset(
                                center.x.toFloat(),
                                center.y.toFloat()
                            ),
                            style = Stroke(width = 4.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.1f),
                            radius = radiusSecond,
                            center = Offset(
                                center.x.toFloat(),
                                center.y.toFloat()
                            )
                        )
                    }
                }
            }

            is MapScreenViewState.StationList -> {
                val stations = (mapViewState as MapScreenViewState.StationList).eventStations
                stations.forEach { station ->
                    CustomMarker(
                        modifier = Modifier
                            .offset {
                                station.coordinates.mapToMap(
                                    xOffset = PIN_OFFSET,
                                    cameraPositionState = cameraPositionState
                                )
                            },
                        station,
                        openedMarker,
                        onOpened = { station ->
                            openedMarker = station
                        },
                        onClickStation = {
                            viewModel.onClickStationLabel(openedMarker?.code!!)
                        }
                    )
                }
                isZoomedToBound =
                    zoomToStationBound(stations, isMapLoaded, isZoomedToBound, cameraPositionState)
            }

            is MapScreenViewState.EventList -> {
                val events = (mapViewState as MapScreenViewState.EventList).events
                val eventsFiltered = remember(events, openedMarker) {
                    if (openedMarker != null) {
                        events.filter { event ->
                            val results = FloatArray(1)
                            if (event.location?.lat != null && event.location?.lng != null) {
                                Location.distanceBetween(
                                    openedMarker!!.coordinates.latitude,
                                    openedMarker!!.coordinates.longitude,
                                    event.location!!.lat!!,
                                    event.location!!.lng!!,
                                    results
                                )
                                results[0] <= 2000f
                            } else {
                                false
                            }
                        }
                    } else {
                        viewModel.displayAllStation()
                        emptyList()
                    }
                }
                val eventsGrouped =
                    remember(eventsFiltered) { eventsFiltered.groupBy { it.location } }
                var openedEvents: Coordinate? by remember { mutableStateOf(null) }
                var openedEventList: Event? by remember { mutableStateOf(null) }
                var isBottomSheetVisible by remember { mutableStateOf(false) }
                var fromEventPin by remember { mutableStateOf(false) }
                val listState = rememberLazyListState()

                ZoomToEventBound(
                    eventsFiltered,
                    cameraPositionState
                )

                eventsGrouped.forEach { eventGroup ->
                    EventPin(
                        openedEvents,
                        eventGroup,
                        cameraPositionState,
                        onClickPin = { event ->
                            openedEvents = event
                        },
                        onClickEvent = { bottomSheetVisible, event, fromPin ->
                            isBottomSheetVisible = bottomSheetVisible
                            openedEventList = event
                            fromEventPin = fromPin
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = uiHeight + 16.dp, horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    ) {
                        Button(
                            modifier = Modifier
                                .size(48.dp),
                            onClick = {
                                viewModel.displayAllStation()
                                openedMarker = null
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
                                painter = painterResource(id = R.drawable.rounded_close_24),
                                contentDescription = null,
                                tint = colorScheme.onSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            modifier = Modifier
                                .size(48.dp),
                            onClick = {
                                isBottomSheetVisible = true
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
                                painter = painterResource(id = R.drawable.event_icon),
                                contentDescription = null,
                                tint = colorScheme.onSecondary
                            )
                        }
                    }
                }

                LaunchedEffect(
                    isBottomSheetVisible,
                    openedEventList,
                    eventsFiltered,
                    fromEventPin
                ) {
                    if (fromEventPin && isBottomSheetVisible && openedEventList != null) {
                        val index = eventsFiltered.indexOf(openedEventList)
                        if (index != -1) {
                            listState.animateScrollToItem(index)
                        }
                        fromEventPin = false
                    }
                }

                LaunchedEffect(eventsFiltered) {
                    if (eventsFiltered.isEmpty()) isBottomSheetVisible = true
                }

                if (isBottomSheetVisible) {
                    ModalBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            isBottomSheetVisible = false
                        },
                        containerColor = colorScheme.surface
                    ) {
                        if (eventsFiltered.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Uh oh! seems like there is no events",
                                    fontSize = 32.sp,
                                    color = colorScheme.onSurface.copy(0.5f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        ListEvent(
                            eventsFiltered,
                            openedEventList,
                            listState
                        ) {
                            openedEventList = it
                        }
                    }
                }
            }

            is MapScreenViewState.ExpandedSearch -> {
                Log.d("MapScreen", "ExpandedSearch")
                navStations = (mapViewState as MapScreenViewState.ExpandedSearch).stations
            }
        }

        AnimatedVisibility(
            visible = backStack.last() == MapScreen && mapViewState !is MapScreenViewState.EventList,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            label = "SearchBarAnimation"
        ) {
            SearchBarCustom(
                modifier = Modifier.align(Alignment.TopCenter),
                textFieldState = textFieldState,
                navigationStations = navStations,
                backdrop = backdrop,
                keyboardController = keyboardController,
                onSearch = { searchString ->
                    backStack.add(TrainNavScreen(searchString))
                },
                onExpandedChange = { isSearchExpanded ->
                    if (isSearchExpanded) viewModel.onExpandedSearch()
                    else viewModel.displayAllStation()
                }
            )
        }

        if (!isMapLoaded) {
            AnimatedVisibility(
                modifier = Modifier.matchParentSize(),
                visible = !isMapLoaded,
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                ContainedLoadingIndicator(
                    modifier = Modifier
                        .background(colorScheme.background)
                        .wrapContentSize()
                )
            }
        }
    }
}

private const val displayLimit = 3

@Composable
private fun EventPin(
    openedEvents: Coordinate?,
    eventGroup: Map.Entry<Coordinate?, List<Event>>,
    cameraPositionState: CameraPositionState,
    onClickPin: (Coordinate?) -> Unit,
    onClickEvent: (Boolean, Event?, Boolean) -> Unit
) {
    val isOpened = openedEvents == eventGroup.key
    val changeSize by animateDpAsState(
        targetValue = if (isOpened) 40.dp else 48.dp
    )
    val pointToCurve by animateDpAsState(
        targetValue = if (isOpened) 6.dp else 0.dp,
    )
    val roundToCurve by animateDpAsState(
        targetValue = if (isOpened) 6.dp else 24.dp,
    )
    val roundToCurveInner by animateDpAsState(
        targetValue = if (isOpened) 4.dp else 22.dp,
    )
    val innerPaddingChange by animateDpAsState(
        targetValue = if (isOpened) 2.dp else 4.dp,
    )
    val iconPaddingChange by animateDpAsState(
        targetValue = if (isOpened) 2.dp else 6.dp
    )

    Column(
        modifier = Modifier
            .offset {
                eventGroup.key?.let {
                    LatLng(
                        it.lat!!,
                        it.lng!!
                    ).mapToMap(
                        yOffset = -80,
                        cameraPositionState = cameraPositionState
                    )
                }!!
            }
    ) {
        AnimatedVisibility(
            visible = isOpened || openedEvents == null,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .size(changeSize)
                        .clickable(
                            onClick = {
                                onClickPin(
                                    if (isOpened) null
                                    else eventGroup.key
                                )
                            }
                        ),
                    shape = RoundedCornerShape(
                        topStart = roundToCurve,
                        topEnd = roundToCurve,
                        bottomEnd = roundToCurve,
                        bottomStart = pointToCurve
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.secondary
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPaddingChange)
                            .clip(
                                RoundedCornerShape(roundToCurveInner)
                            )
                            .background(colorScheme.onSecondary)
                            .padding(iconPaddingChange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxSize(),
                            painter = painterResource(
                                id =
                                    if (!isOpened) R.drawable.event_icon_2
                                    else R.drawable.rounded_close_24
                            ),
                            contentDescription = null,
                            tint = colorScheme.secondary
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                AnimatedVisibility(
                    visible = isOpened
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(0.33f),
                        text = "Interested? Click the events to know more",
                        fontSize = 12.sp,
                        color = colorScheme.onBackground,
                        lineHeight = 12.sp
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = isOpened,
            enter = slideInHorizontally { -it }
        ) {
            Column {
                for (i in 0 until if (eventGroup.value.size > displayLimit) displayLimit else eventGroup.value.size) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .widthIn(max = 300.dp)
                            .background(colorScheme.secondary)
                            .clickable(
                                onClick = {
                                    onClickEvent(true, eventGroup.value[i], true)
                                }
                            )
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        text = eventGroup.value[i].title!!,
                        color = colorScheme.onSecondary,
                    )
                }
                if (eventGroup.value.size > displayLimit) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .widthIn(max = 300.dp)
                            .background(colorScheme.secondary)
                            .clickable(
                                onClick = {
                                    onClickEvent(true, null, true)
                                }
                            )
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        text = "See More...",
                        color = colorScheme.onSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ListEvent(
    events: List<Event>,
    openedEventList: Event?,
    listState: LazyListState,
    onClick: (Event?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .border(
                BorderStroke(2.dp, colorScheme.onSurface),
                RoundedCornerShape(24.dp)
            )
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        state = listState
    ) {
        items(events) { event ->
            val isOpened = event == openedEventList
            if (event == events.first()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Click on the event to see details",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = colorScheme.onSurface.copy(0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            EventDetail(
                event,
                isOpened
            ) {
                onClick(it)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (event == events.last()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "More Events Coming Soon...",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = colorScheme.onSurface.copy(0.75f)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun EventDetail(
    event: Event,
    isOpened: Boolean,
    onClick: (Event?) -> Unit
) {
    val context = LocalContext.current
    val backgroundColorChange by animateColorAsState(
        targetValue = if (isOpened) colorScheme.primary else colorScheme.secondary
    )
    val spacerChange by animateDpAsState(
        targetValue = if (isOpened) 4.dp else 0.dp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(backgroundColorChange)
            .padding(8.dp)
            .clickable(
                enabled = !isOpened,
                onClick = {
                    onClick(event)
                }
            )
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .animateContentSize()
        ) {
            Row {
                AnimatedVisibility(
                    visible = isOpened,
                    enter = slideInHorizontally { -it },
                ) {
                    Text(
                        text = event.title ?: "No title",
                        color = colorScheme.onSecondary,
                        fontSize = 28.sp,
                        lineHeight = 22.sp
                    )
                }
            }
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = !isOpened,
                    exit = shrinkHorizontally { -it }
                ) {
                    VerticalIconBox(painterResource(id = R.drawable.event_icon_2))
                }
                Spacer(modifier = Modifier.width(8.dp))
                AnimatedVisibility(
                    visible = !isOpened,
                    exit = fadeOut() + slideOutHorizontally { it }
                ) {
                    Text(
                        text = event.title ?: "No title",
                        color = colorScheme.onSecondary,
                        fontSize = 24.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        AnimatedVisibility(
            visible = isOpened,
        ) {
            Text(
                text = "DATE",
                color = colorScheme.onPrimary.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
        Box(
            modifier = Modifier
                .animateContentSize()
        ) {
            Row {
                AnimatedVisibility(
                    visible = isOpened,
                    enter = slideInHorizontally { -it },
                ) {
                    Text(
                        text = event.date ?: "Unspecified date",
                        color = colorScheme.onSecondary,
                        fontSize = 24.sp
                    )
                }
            }
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = !isOpened,
                    exit = shrinkHorizontally { -it }
                ) {
                    VerticalIconBox(painterResource(id = R.drawable.event_icon))
                }
                Spacer(Modifier.width(8.dp))
                AnimatedVisibility(
                    visible = !isOpened,
                    exit = fadeOut() + slideOutHorizontally { it }
                ) {
                    Text(
                        text = event.date ?: "Unspecified date",
                        color = colorScheme.onSecondary,
                        fontSize = 20.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        AnimatedVisibility(
            visible = isOpened,
        ) {
            Text(
                text = "VENUE",
                color = colorScheme.onPrimary.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
        Box(
            modifier = Modifier
                .animateContentSize()
        ) {
            Row {
                AnimatedVisibility(
                    visible = isOpened,
                    enter = slideInHorizontally { -it },
                ) {
                    Text(
                        text = event.venue?.name ?: "No specified venue",
                        color = colorScheme.onSecondary,
                        fontSize = 24.sp
                    )
                }
            }
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = !isOpened,
                    exit = shrinkHorizontally { -it }
                ) {
                    VerticalIconBox(painterResource(id = R.drawable.location_icon))
                }
                Spacer(Modifier.width(8.dp))
                AnimatedVisibility(
                    visible = !isOpened,
                    exit = fadeOut() + slideOutHorizontally { it }
                ) {
                    Text(
                        text = event.venue?.name ?: "No specified venue",
                        color = colorScheme.onSecondary,
                        fontSize = 20.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(spacerChange))

        AnimatedVisibility(
            visible = isOpened,
        ) {
            Text(
                text = "DETAILS",
                color = colorScheme.onPrimary.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
        AnimatedVisibility(
            visible = isOpened,
        ) {
            Text(
                text = event.description ?: "No description",
                color = colorScheme.onSecondary,
                fontSize = 18.sp
            )
        }
        AnimatedVisibility(
            visible = isOpened,
        ) {
            Text(
                text = "AVAILABLE TICKETS",
                color = colorScheme.onPrimary.copy(alpha = 0.5f),
                fontSize = 16.sp
            )
        }
        AnimatedVisibility(
            visible = isOpened
        ) {
            Column {
                event.ticketInfo?.forEach { ticketInfo ->
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                            .clickable(
                                enabled = isOpened,
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        ticketInfo?.link?.toUri()
                                    )
                                    context.startActivity(intent)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = ticketInfo?.source!!,
                            fontSize = 24.sp,
                            color = colorScheme.onSecondary
                        )
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(R.drawable.arrow_right_icon),
                            tint = colorScheme.onSecondary,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerticalIconBox(
    icon: Painter
) {
    Box(
        modifier = Modifier
            .heightIn(min = 40.dp)
            .fillMaxHeight()
            .width(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.tertiary)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(28.dp),
            painter = icon,
            contentDescription = null,
            tint = colorScheme.onTertiary
        )
    }
}

@Composable
private fun zoomToStationBound(
    eventStations: List<EventStation>,
    isMapLoaded: Boolean,
    zoomToBound: Boolean,
    cameraPositionState: CameraPositionState
): Boolean {
    var isZoomedToBound = zoomToBound
    LaunchedEffect(eventStations, isMapLoaded) {
        if (eventStations.isNotEmpty() && !isZoomedToBound && isMapLoaded) {
            val boundsBuilder = LatLngBounds.builder()
            for (station in eventStations) {
                boundsBuilder.include(station.coordinates)
            }
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    160
                ),
                durationMs = 1500
            )
            isZoomedToBound = true
        }
    }
    return isZoomedToBound
}

@Composable
private fun ZoomToEventBound(
    events: List<Event>,
    cameraPositionState: CameraPositionState
) {
    LaunchedEffect(events) {
        if (events.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            events.forEach { event ->
                boundsBuilder.include(
                    LatLng(
                        event.location?.lat!!,
                        event.location?.lng!!
                    )
                )
            }
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngBounds(
                    boundsBuilder.build(),
                    160
                ),
                durationMs = 1500
            )
        }
    }
}