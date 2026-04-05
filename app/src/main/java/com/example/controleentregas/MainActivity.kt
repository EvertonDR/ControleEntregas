package com.example.controleentregas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
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
    object NaoPagas : Screen("nao_pagas", "Não Pagas", Icons.Default.MoneyOff)
    object Pagas : Screen("pagas", "Pagas", Icons.Default.Done)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControleEntregasTheme {
                MainApp(onPermissionsRequested = { /* Permissões tratadas pelo sistema moderno */ })
            }
        }
    }
}

@Composable
fun MainApp(
    onPermissionsRequested: () -> Unit,
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(Screen.EmAberto, Screen.Realizadas, Screen.NaoPagas, Screen.Pagas)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    
    val showFab = currentRoute == Screen.EmAberto.route
    var isExpanded by remember { mutableStateOf(false) }
    var showGlobalDatePicker by remember { mutableStateOf(false) }

    // Lançador para escolher o arquivo JSON
    val context = LocalContext.current
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { viewModel.importarBackup(context, it) }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.take(2).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, null) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, null) },
                    label = { Text("Filtrar") },
                    selected = false,
                    onClick = { showGlobalDatePicker = true }
                )

                bottomNavItems.takeLast(2).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, null) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isExpanded) {
                        FloatingActionButton(onClick = { navController.navigate("create_delivery_screen"); isExpanded = false }) { Icon(Icons.Default.Motorcycle, null) }
                        FloatingActionButton(onClick = { navController.navigate("create_cliente_screen"); isExpanded = false }) { Icon(Icons.Default.PersonAdd, null) }
                        FloatingActionButton(onClick = { navController.navigate("create_bairro_screen"); isExpanded = false }) { Icon(Icons.Default.AddLocation, null) }
                    }
                    FloatingActionButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(if (isExpanded) Icons.Default.Close else Icons.Default.Add, null)
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, Screen.EmAberto.route, Modifier.padding(innerPadding)) {
            composable(Screen.EmAberto.route) { 
                MainScreen(
                    onBackupClick = { viewModel.exportarBackupTotal(context) },
                    onImportClick = { importLauncher.launch(arrayOf("application/json")) },
                    viewModel = viewModel
                ) 
            }
            composable(Screen.Realizadas.route) { RealizadasScreen(viewModel) }
            composable(Screen.NaoPagas.route) { NaoPagasScreen(viewModel) }
            composable(Screen.Pagas.route) { PagasScreen(navController, viewModel) }
            composable("caixa_screen") { CaixaScreen(viewModel) }
            composable("create_delivery_screen") { CreateDeliveryScreen(navController, viewModel) }
            composable("create_bairro_screen") { CreateBairroScreen(navController, viewModel) }
            composable("create_cliente_screen") { CreateClienteScreen(navController, viewModel) }
        }
    }

    if (showGlobalDatePicker) {
        AlertDialog(
            onDismissRequest = { showGlobalDatePicker = false },
            title = { Text("Filtrar por Data") },
            text = { Text("Escolha uma opção para a aba atual:") },
            confirmButton = {
                Button(onClick = {
                    showGlobalDatePicker = false
                    val cal = Calendar.getInstance()
                    val dpd = android.app.DatePickerDialog(
                        navController.context,
                        { _, y, m, d ->
                            cal.set(y, m, d)
                            when (currentRoute) {
                                Screen.EmAberto.route -> viewModel.setFiltroData(cal.time)
                                Screen.Realizadas.route -> viewModel.setFiltroDataRealizadas(cal.time)
                                Screen.Pagas.route -> viewModel.setFiltroDataPagas(cal.time)
                            }
                        },
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.show()
                }) { Text("Selecionar Data") }
            },
            dismissButton = {
                TextButton(onClick = {
                    when (currentRoute) {
                        Screen.EmAberto.route -> viewModel.setFiltroData(null)
                        Screen.Realizadas.route -> viewModel.setFiltroDataRealizadas(null)
                        Screen.Pagas.route -> viewModel.setFiltroDataPagas(null)
                    }
                    showGlobalDatePicker = false
                }) { Text("Limpar Filtro") }
            }
        )
    }
}

@Composable
fun MainScreen(
    onBackupClick: () -> Unit,
    onImportClick: () -> Unit,
    viewModel: MainViewModel
) {
    val uiState by viewModel.mainUiState.collectAsState()
    val filtro by viewModel.filtroData.collectAsState()
    var showBackupMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var entregaToDelete by remember { mutableStateOf<EntregaDisplay?>(null) }

    if (showDeleteDialog && entregaToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Apagar Entrega") },
            text = { Text("Tem certeza?") },
            confirmButton = { Button(onClick = { viewModel.deleteEntrega(entregaToDelete!!.originalEntrega); showDeleteDialog = false }) { Text("Sim") } },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Não") } }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = filtro ?: "Total em Aberto", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "R$ ${String.format("%.2f", uiState.total)}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Box {
                IconButton(onClick = { showBackupMenu = true }) {
                    Icon(Icons.Default.Download, contentDescription = "Opções de Backup")
                }
                DropdownMenu(
                    expanded = showBackupMenu,
                    onDismissRequest = { showBackupMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Salvar Backup") },
                        onClick = {
                            onBackupClick()
                            showBackupMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Save, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Importar Backup") },
                        onClick = {
                            onImportClick()
                            showBackupMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Upload, null) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Body(uiState.entregas, { viewModel.togglePagoStatus(it.originalEntrega) }, { viewModel.toggleRealizadaStatus(it.originalEntrega) }) {
            entregaToDelete = it
            showDeleteDialog = true
        }
    }
}

@Composable
fun Body(entregas: List<EntregaDisplay>, onPago: (EntregaDisplay) -> Unit, onRealizada: (EntregaDisplay) -> Unit, onDelete: (EntregaDisplay) -> Unit) {
    if (entregas.isEmpty()) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Vazio") }
    else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(entregas, key = { it.id }) { EntregaItem(it, onPago, onRealizada, onDelete) }
    }
}

@Composable
fun EntregaItem(item: EntregaDisplay, onPago: (EntregaDisplay) -> Unit, onRealizada: (EntregaDisplay) -> Unit, onDelete: (EntregaDisplay) -> Unit) {
    var showPagoDialog by remember { mutableStateOf(false) }
    var showRealDialog by remember { mutableStateOf(false) }

    if (showPagoDialog) AlertDialog(onDismissRequest = { showPagoDialog = false }, title = { Text("Pagamento") }, text = { Text("Alterar status?") }, confirmButton = { Button(onClick = { onPago(item); showPagoDialog = false }) { Text("Sim") } }, dismissButton = { TextButton(onClick = { showPagoDialog = false }) { Text("Não") } })
    if (showRealDialog) AlertDialog(onDismissRequest = { showRealDialog = false }, title = { Text("Realização") }, text = { Text("Alterar status?") }, confirmButton = { Button(onClick = { onRealizada(item); showRealDialog = false }) { Text("Sim") } }, dismissButton = { TextButton(onClick = { showRealDialog = false }) { Text("Não") } })

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(item.pago, { showPagoDialog = true }); Text("Paga", fontSize = 12.sp) }
                Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(item.realizada, { showRealDialog = true }); Text("Realizada", fontSize = 12.sp) }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(item.clienteNome, fontWeight = FontWeight.Bold)
                Text("${item.bairroNome} - ${item.cidade}", fontSize = 14.sp)
                Text(item.data, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = { onDelete(item) }) { Icon(Icons.Default.Delete, null) }
                Text("R$ ${String.format("%.2f", item.valor)}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
