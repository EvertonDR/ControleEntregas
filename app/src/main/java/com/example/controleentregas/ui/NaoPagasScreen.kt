package com.example.controleentregas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
fun NaoPagasScreen(
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val naoPagasUiState by viewModel.naoPagasUiState.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Total a Receber", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("R$ ${String.format("%.2f", naoPagasUiState.totalNaoPago)}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.padding(8.dp))

        if (naoPagasUiState.entregasNaoPagasPorCliente.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Nenhuma entrega não paga encontrada")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                naoPagasUiState.entregasNaoPagasPorCliente.forEach { (clienteNome, entregas) ->
                    val subtotal = naoPagasUiState.subtotalPorCliente[clienteNome] ?: 0.0
                    item {
                        ClienteNaoPagasSection(
                            clienteNome = clienteNome, 
                            entregas = entregas, 
                            subtotal = subtotal,
                            viewModel = viewModel,
                            onDownloadClick = {
                                viewModel.exportarNaoPagasCliente(context, clienteNome)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClienteNaoPagasSection(clienteNome: String, entregas: List<EntregaDisplay>, subtotal: Double, viewModel: MainViewModel, onDownloadClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$clienteNome (${entregas.size}) - R$ ${String.format("%.2f", subtotal)}",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = onDownloadClick) {
                Icon(Icons.Default.Download, contentDescription = "Baixar Relatório de Cobrança")
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Recolher" else "Expandir"
            )
        }

        if (expanded) {
            Column(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                entregas.forEach { entrega ->
                    EntregaNaoPagaItem(
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
fun EntregaNaoPagaItem(
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
