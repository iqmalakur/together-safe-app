package com.togethersafe.app.views.map

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.GeocodingViewModel

@Composable
fun MapHeader(
    geocodingViewModel: GeocodingViewModel = hiltViewModel(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val animationSpec = tween<Float>(durationMillis = 200)
    val locationResult by geocodingViewModel.locationResult.collectAsState()
    val error by geocodingViewModel.error.collectAsState()
    var isSearching by remember { mutableStateOf(false) }
    val headerBackgroundAlpha by animateFloatAsState(
        targetValue = if (isSearching) 1f else 0f,
        animationSpec = animationSpec,
        label = "Header Background Alpha",
    )

    LaunchedEffect(error) { if (error != null) appViewModel.setToastMessage(error!!) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) { detectTapGestures {} }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(Color.White.copy(alpha = headerBackgroundAlpha))
                }
                .padding(horizontal = 5.dp, vertical = 15.dp),
        ) {
            HeaderButton(isSearching)
            SearchBar(
                modifier = Modifier
                    .testTag("SearchBar")
                    .weight(1f),
                setIsSearching = { state -> isSearching = state },
                onSearch = { keyword -> geocodingViewModel.search(keyword) }
            )
        }

        if (isSearching) {
            BackHandler { isSearching = false }
        }

        AnimatedVisibility(
            visible = isSearching,
            enter = fadeIn(animationSpec),
            exit = fadeOut(animationSpec),
        ) { SearchResult(locationResult) }
    }
}
