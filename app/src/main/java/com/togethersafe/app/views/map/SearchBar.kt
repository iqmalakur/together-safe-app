package com.togethersafe.app.views.map

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
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

    TextField(
        value = searchValue,
        onValueChange = { searchValue = it },
        placeholder = { Text("Telusuri di sini") },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
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
            .onFocusChanged { setIsSearching(it.isFocused) },
    )
}
