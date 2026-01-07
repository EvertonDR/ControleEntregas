package com.example.controleentregas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Date

@Composable
fun Header(
    defaultTitle: String,
    total: Double,
    filtro: String?,
    onFilterClick: (Date) -> Unit,
    onClearFilter: () -> Unit,
    onBackupClick: (() -> Unit)? = null
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = filtro ?: defaultTitle, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "R$ ${String.format("%.2f", total)}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBackupClick != null) {
                IconButton(onClick = onBackupClick) {
                    Icon(Icons.Default.Download, contentDescription = "Baixar Backup Total")
                }
            }
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Filtrar por data")
            }
            IconButton(onClick = onClearFilter, enabled = filtro != null) {
                Icon(Icons.Default.Clear, contentDescription = "Limpar filtro")
            }
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSet = { year, month, day ->
                calendar.set(year, month, day)
                onFilterClick(calendar.time)
                showDatePicker = false
            },
            initialDate = calendar
        )
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSet: (Int, Int, Int) -> Unit,
    initialDate: Calendar
) {
    val context = LocalContext.current
    val year = initialDate.get(Calendar.YEAR)
    val month = initialDate.get(Calendar.MONTH)
    val day = initialDate.get(Calendar.DAY_OF_MONTH)

    val dialog = android.app.DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            onDateSet(selectedYear, selectedMonth, selectedDay)
        },
        year,
        month,
        day
    )
    dialog.setOnDismissListener { onDismissRequest() }
    DisposableEffect(Unit) {
        dialog.show()
        onDispose { dialog.dismiss() }
    }
}