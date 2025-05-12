package com.togethersafe.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.togethersafe.app.utils.getFormattedIncidentRisk
import com.togethersafe.app.utils.getViewModel
import com.togethersafe.app.viewmodels.IncidentViewModel

@Composable
fun IncidentWarningCard() {
    val incidentViewModel: IncidentViewModel = getViewModel()

    val activeIncidentArea by incidentViewModel.activeIncidentArea.collectAsState()
    var isClosed by remember { mutableStateOf(false) }

    LaunchedEffect(activeIncidentArea) {
        isClosed = activeIncidentArea == null
    }

    if (isClosed) return

    activeIncidentArea?.let { incident ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ Peringatan Insiden!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Kategori Insiden: ${incident.category}")
                    Text("Level Risiko: ${getFormattedIncidentRisk(incident.riskLevel)}")
                }

                IconButton(
                    onClick = { isClosed = true },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}


