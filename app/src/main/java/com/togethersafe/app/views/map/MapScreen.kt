package com.togethersafe.app.views.map

import androidx.compose.runtime.Composable

@Composable
fun MapScreen() {
    NavigationDrawer {
        Map()
        MapHeader()
    }
}
