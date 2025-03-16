package com.togethersafe.app.views.map

import androidx.compose.runtime.Composable
import com.togethersafe.app.components.DoubleBackHandler

@Composable
fun MapScreen() {
    NavigationDrawer {
        Map()
        MapHeader()
    }

    DoubleBackHandler()
}
