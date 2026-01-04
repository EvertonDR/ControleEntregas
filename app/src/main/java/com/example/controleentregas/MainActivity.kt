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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.controleentregas.ui.*
import com.example.controleentregas.ui.theme.ControleEntregasTheme
import java.util.Calendar
import java.util.Date

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object EmAberto : Screen("em_aberto", "Em Aberto", Icons.Default.List)
    object Realizadas : Screen("realizadas", "Realizadas", Icons.Default.CheckCircle)
    object Pagas : Screen("pagas", "Pagas", Icons.Default.Done)
}

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
    val bottomNavItems = listOf(Screen.EmAberto, Screen.Realizadas, Screen.Pagas)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showFab = currentDestination?.route == Screen.EmAberto.route
    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (showFab) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isExpanded) {
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("create_delivery_screen")
                                isExpanded = false
                            },
                        ) {
                            Icon(Icons.Default.Motorcycle, "Adicionar Entrega")
                        }
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("create_cliente_screen")
                                isExpanded = false
                            },
                        ) {
                            Icon(Icons.Default.PersonAdd, "Adicionar Cliente")
                        }
                        FloatingActionButton(
                            onClick = {
                                navController.navigate("create_bairro_screen")
                                isExpanded = false
                            },
                        ) {
                            Icon(Icons.Default.AddLocation, "Adicionar Bairro")
                        }
                    }
                    FloatingActionButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (isExpanded) "Fechar" else "Adicionar"
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.EmAberto.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screen.EmAberto.route) {
                MainScreen(
                    onPermissionsRequested = onPermissionsRequested
                )
            }
            composable(Screen.Realizadas.route) {
                RealizadasScreen()
            }
            composable(Screen.Pagas.route) {
                PagasScreen()
            }
            composable("create_delivery_screen") {
                CreateDeliveryScreen(navController)
            }
            composable("create_bairro_screen") {
                CreateBairroScreen(navController)
            }
            composable("create_cliente_screen") {
                CreateClienteScreen(navController)
            }
        }
    }
}

@Composable
fun MainScreen(
    onPermissionsRequested: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val mainUiState by viewModel.mainUiState.collectAsState()
    val filtroData by viewModel.filtroData.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
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
        Body(
            entregas = mainUiState.entregas,
            onPagoChange = { entrega -> viewModel.marcarComoPaga(entrega.originalEntrega) },
            onRealizadaChange = { entrega -> viewModel.marcarComoRealizada(entrega.originalEntrega) }
        )
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
fun Body(
    entregas: List<EntregaDisplay>,
    onPagoChange: (EntregaDisplay) -> Unit,
    onRealizadaChange: (EntregaDisplay) -> Unit
) {
    if (entregas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Nenhuma entrega encontrada")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entregas, key = { it.id }) {
                EntregaItem(
                    entrega = it,
                    onPagoChange = { onPagoChange(it) },
                    onRealizadaChange = { onRealizadaChange(it) }
                )
            }
        }
    }
}

@Composable
fun EntregaItem(
    entrega: EntregaDisplay,
    onPagoChange: () -> Unit,
    onRealizadaChange: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = entrega.pago, onCheckedChange = { onPagoChange() })
                    Text("Paga", fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = entrega.realizada, onCheckedChange = { onRealizadaChange() })
                    Text("Realizada", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = entrega.clienteNome, fontWeight = FontWeight.Bold)
                Text(text = entrega.bairroNome, fontSize = 14.sp)
                Text(text = "Data: ${entrega.data}", fontSize = 14.sp)
            }
            Text(text = "R$ ${String.format("%.2f", entrega.valor)}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}
