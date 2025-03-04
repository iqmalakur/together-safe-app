package com.togethersafe.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.togethersafe.app.ui.viewmodel.GeocodingViewModel
import kotlinx.coroutines.delay

@Composable
fun MapHeader(geocodingViewModel: GeocodingViewModel = hiltViewModel()) {
    val locationResult by geocodingViewModel.locationResult.collectAsState()

    LaunchedEffect(locationResult) {
        // TODO: action for location result
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundedIconButton(
            onClick = { /* TODO: Implementasi menu */ },
            imageVector = Icons.Rounded.Menu,
            contentDescription = "Menu",
        )
        SearchBar(modifier = Modifier.weight(1f)) { keyword ->
            geocodingViewModel.search(keyword)
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier, onSearch: (String) -> Unit) {
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
            .clip(CircleShape),
    )
}

