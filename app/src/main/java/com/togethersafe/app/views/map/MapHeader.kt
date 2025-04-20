package com.togethersafe.app.views.map

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.AppViewModel
import com.togethersafe.app.viewmodels.GeolocationViewModel

@Composable
fun MapHeader() {
    val geolocationViewModel: GeolocationViewModel = getViewModel()
    val appViewModel: AppViewModel = getViewModel()

    val animationSpec = tween<Float>(durationMillis = 200)
    val focusManager = LocalFocusManager.current

    val locationResult by geolocationViewModel.locationResult.collectAsState()

    var isSearching by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val headerBackgroundAlpha by animateFloatAsState(
        targetValue = if (isSearching) 1f else 0f,
        animationSpec = animationSpec,
        label = "Header Background Alpha",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // prevent map dragging on the map header when the user is searching
            .then(
                if (isSearching) Modifier.pointerInput(Unit) { detectTapGestures {} }
                else Modifier
            )
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
                isLoading = isLoading,
                setIsSearching = { isSearching = it },
                setIsLoading = { isLoading = it },
                onSearch = {
                    geolocationViewModel.search(
                        query = it,
                        onError = { error -> appViewModel.setToastMessage(error) },
                        onComplete = { isLoading = false },
                    )
                },
            )
        }

        if (isSearching) {
            BackHandler {
                focusManager.clearFocus()
                isSearching = false
            }
        }

        AnimatedVisibility(
            visible = isSearching,
            enter = fadeIn(animationSpec),
            exit = fadeOut(animationSpec),
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                SearchResult(locationResult)
            }
        }
    }
}
