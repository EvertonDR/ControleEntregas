package com.example.controleentregas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RealizadasScreen(
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val realizadasUiState by viewModel.realizadasUiState.collectAsState()
    val filtroData by viewModel.filtroDataRealizadas.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Header(
            defaultTitle = "Total Realizadas",
            total = realizadasUiState.totalRealizadas,
            filtro = filtroData,
            onFilterClick = { viewModel.setFiltroDataRealizadas(it) },
            onClearFilter = { viewModel.setFiltroDataRealizadas(null) }, // Correção: usando setFiltroDataRealizadas(null)
            showFilterActions = false // Esconde os filtros do topo, agora usamos o botão central
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (realizadasUiState.entregasPorCliente.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Nenhuma entrega realizada encontrada")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                realizadasUiState.entregasPorCliente.forEach { (clienteNome, entregas) ->
                    item {
                        ClienteRealizadasSection(
                            clienteNome = clienteNome,
                            entregas = entregas,
                            onDownloadClick = {
                                viewModel.exportarResumoCliente(context, clienteNome)
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClienteRealizadasSection(
    clienteNome: String,
    entregas: List<EntregaDisplay>,
    onDownloadClick: () -> Unit,
    viewModel: MainViewModel
) {
    var isClienteExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isClienteExpanded = !isClienteExpanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$clienteNome (${entregas.size})",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = onDownloadClick) {
                Icon(Icons.Default.Download, contentDescription = "Baixar Resumo")
            }
            Icon(
                imageVector = if (isClienteExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isClienteExpanded) "Recolher" else "Expandir"
            )
        }

        if (isClienteExpanded) {
            val (entregasPagas, entregasNaoPagas) = entregas.partition { it.pago }

            Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                EntregasSubSection(title = "Pagas (${entregasPagas.size})", entregas = entregasPagas, viewModel = viewModel)
                EntregasSubSection(title = "Não Pagas (${entregasNaoPagas.size})", entregas = entregasNaoPagas, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun EntregasSubSection(
    title: String, 
    entregas: List<EntregaDisplay>,
    viewModel: MainViewModel
) {
    var isSubSectionExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isSubSectionExpanded = !isSubSectionExpanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = if (isSubSectionExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isSubSectionExpanded) "Recolher" else "Expandir"
            )
        }

        if (isSubSectionExpanded) {
            Column(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                entregas.forEach { entrega ->
                    EntregaRealizadaItem(
                        entrega = entrega,
                        onPagoChange = { viewModel.togglePagoStatus(entrega.originalEntrega) },
                        onRealizadaChange = { viewModel.toggleRealizadaStatus(entrega.originalEntrega) }
                    )
                }
            }
        }
    }
}

@Composable
fun EntregaRealizadaItem(
    entrega: EntregaDisplay,
    onPagoChange: () -> Unit,
    onRealizadaChange: () -> Unit
) {
    var showPagoDialog by remember { mutableStateOf(false) }
    var showRealizadaDialog by remember { mutableStateOf(false) }

    if (showPagoDialog) {
        AlertDialog(
            onDismissRequest = { showPagoDialog = false },
            title = { Text("Confirmar Pagamento") },
            text = { Text("Deseja alterar o status de pagamento desta entrega?") },
            confirmButton = {
                TextButton(onClick = { onPagoChange(); showPagoDialog = false }) { Text("Sim") }
            },
            dismissButton = {
                TextButton(onClick = { showPagoDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showRealizadaDialog) {
        AlertDialog(
            onDismissRequest = { showRealizadaDialog = false },
            title = { Text("Confirmar Realização") },
            text = { Text("Deseja alterar o status de realização desta entrega?") },
            confirmButton = {
                TextButton(onClick = { onRealizadaChange(); showRealizadaDialog = false }) { Text("Sim") }
            },
            dismissButton = {
                TextButton(onClick = { showRealizadaDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = entrega.pago, onCheckedChange = { showPagoDialog = true })
                    Text("Paga", fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = entrega.realizada, onCheckedChange = { showRealizadaDialog = true })
                    Text("Realizada", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Data: ${entrega.data}", fontSize = 14.sp)
                Text(text = "${entrega.bairroNome} - ${entrega.cidade}", fontSize = 14.sp)
            }
            Text(
                text = "R$ ${String.format("%.2f", entrega.valor)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
