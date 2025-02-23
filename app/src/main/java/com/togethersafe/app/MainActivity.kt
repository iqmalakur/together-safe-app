package com.togethersafe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.togethersafe.app.ui.components.MapButtons
import com.togethersafe.app.ui.components.MapHeader
import com.togethersafe.app.ui.screens.MapScreen
import com.togethersafe.app.utils.MapConfig.ZOOM_MAX
import com.togethersafe.app.utils.MapConfig.ZOOM_MIN
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_STEP

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var zoomState by remember { mutableDoubleStateOf(ZOOM_DEFAULT) }
            var showLocationState by remember { mutableStateOf(false) }

            Box {
                MapScreen(
                    zoom = zoomState,
                    showLocation = showLocationState,
                    resetShowLocation = { showLocationState = false },
                )
                MapHeader()
                MapButtons(
                    showLocationClick = { showLocationState = true },
                    zoomInClick = { if (zoomState < ZOOM_MAX) zoomState += ZOOM_STEP },
                    zoomOutClick = { if (zoomState > ZOOM_MIN) zoomState -= ZOOM_STEP },
                )
            }
        }
    }
}
