package com.togethersafe.app.views.report

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val today = calendar.time
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time

    val dateFormatValue = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
    val dateFormatLabel = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    val todayPair = dateFormatValue.format(today) to "${dateFormatLabel.format(today)} (Hari ini)"
    val yesterdayPair =
        dateFormatValue.format(yesterday) to "${dateFormatLabel.format(yesterday)} (Kemarin)"
    val options = listOf(yesterdayPair, todayPair)

    var expanded by remember { mutableStateOf(false) }

    val selectedLabel = options.find { it.first == value }?.second ?: "Pilih Tanggal"

    LaunchedEffect(Unit) {
        value.ifEmpty { onValueChange(todayPair.first) }
    }

    PickerFieldContainer(
        label = selectedLabel,
        icon = Icons.Default.DateRange,
        onClick = { expanded = true },
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (dateValue, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        onValueChange(dateValue)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimePickerField(value: String, onValueChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            onValueChange("${zeroPad(hour)}:${zeroPad(minute)}")
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    LaunchedEffect(Unit) {
        value.ifEmpty {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            onValueChange("${zeroPad(hour)}:${zeroPad(minute)}")
        }
    }

    PickerFieldContainer(
        label = value.ifEmpty { "Pilih Waktu" },
        icon = Icons.Default.AccessTime,
        onClick = { timePickerDialog.show() }
    )
}

@Composable
private fun PickerFieldContainer(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    block: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                color = if (label.contains("Pilih")) Color.Gray else Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black
            )
        }

        block?.invoke()
    }
}

private fun zeroPad(number: Int, length: Int = 2) = number.toString().padStart(length, '0')
