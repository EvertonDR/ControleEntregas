package com.example.controleentregas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.controleentregas.data.EntregaEntity
import com.example.controleentregas.ui.AppViewModelProvider
import com.example.controleentregas.ui.CreateDeliveryScreen
import com.example.controleentregas.ui.MainViewModel
import com.example.controleentregas.ui.theme.ControleEntregasTheme
import java.util.Calendar
import java.util.Date

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted.
        } else {
            // Explain to the user that the feature is unavailable
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControleEntregasTheme {
                MainApp(onPermissionsRequested = { askForPermissions() })
            }
        }
    }

    private fun askForPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}

@Composable
fun MainApp(onPermissionsRequested: () -> Unit) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = "main_screen",
            modifier = Modifier.padding(it)
        ) {
            composable("main_screen") {
                MainScreen(
                    navController = navController,
                    onPermissionsRequested = onPermissionsRequested
                )
            }
            composable("create_delivery_screen") {
                CreateDeliveryScreen(navController)
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavController,
    onPermissionsRequested: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val mainUiState by viewModel.mainUiState.collectAsState()
    val filtroData by viewModel.filtroData.collectAsState()
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_delivery_screen") }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Entrega")
            }
        }
    ) {
        Column(modifier = Modifier.padding(it).padding(16.dp)) {
            Header(
                total = mainUiState.total,
                filtro = filtroData,
                onFilterClick = viewModel::setFiltroData,
                onClearFilter = viewModel::limparFiltro,
                onExportClick = {
                    onPermissionsRequested()
                    viewModel.exportarPdf(context)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Body(entregas = mainUiState.entregas, onCheckedChange = viewModel::marcarComoPaga)
        }
    }
}

@Composable
fun Header(
    total: Double,
    filtro: String?,
    onFilterClick: (Date) -> Unit,
    onClearFilter: () -> Unit,
    onExportClick: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = filtro ?: "Total em Aberto", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "R$ ${String.format("%.2f", total)}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Row {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Filtrar por data")
            }
            IconButton(onClick = onClearFilter, enabled = filtro != null) {
                Icon(Icons.Default.Clear, contentDescription = "Limpar filtro")
            }
            IconButton(onClick = onExportClick) {
                Icon(Icons.Default.Share, contentDescription = "Exportar PDF")
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

@Composable
fun Body(entregas: List<EntregaEntity>, onCheckedChange: (EntregaEntity) -> Unit) {
    if (entregas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Nenhuma entrega encontrada")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entregas, key = { it.id }) {
                EntregaItem(entrega = it, onCheckedChange = { onCheckedChange(it) })
            }
        }
    }
}

@Composable
fun EntregaItem(entrega: EntregaEntity, onCheckedChange: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = entrega.pago, onCheckedChange = { onCheckedChange() })
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Cliente ID: ${entrega.clienteId}", fontWeight = FontWeight.Bold)
                Text(text = "Bairro ID: ${entrega.bairroId}", fontSize = 14.sp)
                Text(text = "Data: ${entrega.data}", fontSize = 14.sp)
            }
            Text(text = "R$ ${String.format("%.2f", entrega.valor)}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}
