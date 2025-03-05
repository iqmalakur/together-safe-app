package com.togethersafe.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.ui.viewmodel.AppViewModel
import com.togethersafe.app.ui.viewmodel.GeocodingViewModel
import com.togethersafe.app.ui.viewmodel.MapViewModel
import kotlinx.coroutines.delay

@Composable
fun MapHeader(
    geocodingViewModel: GeocodingViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current
    val locationResult by geocodingViewModel.locationResult.collectAsState()
    val error by geocodingViewModel.error.collectAsState()
    var isSearching by remember { mutableStateOf(false) }
    val animationDuration = 200
    val headerBackgroundAlpha by animateFloatAsState(
        targetValue = if (isSearching) 1f else 0f,
        animationSpec = tween(durationMillis = animationDuration),
        label = "Header Background Alpha",
    )

    LaunchedEffect(error) { if (error != null) appViewModel.setToastMessage(error!!) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) { detectTapGestures {} }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(Color.White.copy(alpha = headerBackgroundAlpha))
                }
                .padding(horizontal = 5.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSearching) {
                RoundedIconButton(
                    bordered = true,
                    onClick = { focusManager.clearFocus() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            } else {
                RoundedIconButton(
                    bordered = true,
                    onClick = { /* TODO: Implementasi menu */ },
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Menu",
                )
            }
            SearchBar(
                modifier = Modifier.weight(1f),
                setIsSearching = { state -> isSearching = state },
                onSearch = { keyword -> geocodingViewModel.search(keyword) }
            )
        }

        AnimatedVisibility(
            visible = isSearching,
            enter = fadeIn(animationSpec = tween(durationMillis = animationDuration)),
            exit = fadeOut(animationSpec = tween(durationMillis = animationDuration)),
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(10.dp)
                    .heightIn(max = 400.dp)
                    .wrapContentHeight()
                    .pointerInput(Unit) { detectTapGestures {} }
            ) {
                items(locationResult) { geocodingLocation ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val location = geocodingLocation.getLocationPoint()
                                mapViewModel.setDestination(location)
                                mapViewModel.setCameraPosition(
                                    location.latitude(), location.longitude()
                                )
                                focusManager.clearFocus()
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = geocodingLocation.name, fontWeight = FontWeight.Bold)
                            Text(text = geocodingLocation.display_name, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    setIsSearching: (state: Boolean) -> Unit,
    onSearch: (String) -> Unit
) {
    var searchValue by rememberSaveable { mutableStateOf("") }
    var debounceSearchValue by remember { mutableStateOf("") }

    LaunchedEffect(searchValue) {
        delay(500)
        if (searchValue != debounceSearchValue) {
            debounceSearchValue = searchValue
            onSearch(searchValue)
        }
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    TextField(
        value = searchValue,
        onValueChange = { searchValue = it },
        placeholder = { Text("Telusuri di sini") },
        colors = textFieldColors,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray
            )
        },
        modifier = modifier
            .padding(horizontal = 5.dp)
            .clip(CircleShape)
            .border(1.dp, Color.Gray, CircleShape)
            .onFocusChanged { focusState ->
                setIsSearching(focusState.isFocused)
            },
    )
}

