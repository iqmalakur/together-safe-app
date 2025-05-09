package com.togethersafe.app.views.map

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import com.togethersafe.app.components.DoubleBackHandler
import com.togethersafe.app.components.IncidentWarningCard

@Composable
fun MapScreen() {
    NavigationDrawer {
        Box {
            Map()
            MapHeader()
            IncidentWarningCard()
        }
    }

    DoubleBackHandler()
}
