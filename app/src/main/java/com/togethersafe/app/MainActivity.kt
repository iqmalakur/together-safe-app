package com.togethersafe.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
            var searchValue by remember { mutableStateOf("") }

            Box {
                MapScreen(
                    zoom = zoomState,
                    showLocation = showLocationState,
                    resetShowLocation = { showLocationState = false },
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp, 15.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    MapControlButton(
                        onClick = { },
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = "Menu",
                    )
                    TextField(
                        value = searchValue,
                        onValueChange = { searchValue = it },
                        placeholder = { Text("Telusuri di sini") },
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
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp, 0.dp)
                            .clip(CircleShape)
                    )
                }
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
        MapControlButton(
            imageVector = Icons.Rounded.MyLocation,
            contentDescription = "Lokasi Saya",
            onClick = showLocationClick
        )
        MapControlButton(
            imageVector = Icons.Rounded.ZoomIn,
            contentDescription = "Perbesar Peta",
            onClick = zoomInClick
        )
        MapControlButton(
            imageVector = Icons.Rounded.ZoomOut,
            contentDescription = "Perkecil Peta",
            onClick = zoomOutClick
        )
    }
}

@Composable
fun MapControlButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .padding(5.dp)
            .clip(CircleShape)
            .background(Color.White),
        onClick = onClick
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}
