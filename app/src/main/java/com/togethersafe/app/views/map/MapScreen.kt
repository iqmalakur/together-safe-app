package com.togethersafe.app.views.map

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import com.togethersafe.app.components.DoubleBackHandler

@Composable
fun MapScreen() {
    NavigationDrawer {
        Box {
            Map()
            MapHeader()
            LocationInfoOverlay()
        }
    }

    DoubleBackHandler()
}
