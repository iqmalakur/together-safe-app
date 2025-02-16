package com.togethersafe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.togethersafe.app.ui.screens.MapScreen
import com.togethersafe.app.utils.MapConfig.ZOOM_MAX
import com.togethersafe.app.utils.MapConfig.ZOOM_MIN
import com.togethersafe.app.utils.MapConfig.ZOOM_DEFAULT
import com.togethersafe.app.utils.MapConfig.ZOOM_STEP

class MainActivity: ComponentActivity() {
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
                MapButtons(
                    showLocationClick = { showLocationState = true },
                    zoomInClick = { if (zoomState < ZOOM_MAX) zoomState += ZOOM_STEP },
                    zoomOutClick = { if (zoomState > ZOOM_MIN) zoomState -= ZOOM_STEP },
                )
            }
        }
    }
}

@Composable
fun MapButtons(
    showLocationClick: () -> Unit,
    zoomInClick: () -> Unit,
    zoomOutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Button(onClick = showLocationClick) { Text("Show My Location") }
        Button(onClick = zoomInClick) { Text("Zoom In") }
        Button(onClick = zoomOutClick) { Text("Zoom Out") }
    }
}
