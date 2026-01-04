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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    val context = LocalContext.current

    if (realizadasUiState.entregasPorCliente.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Nenhuma entrega realizada encontrada")
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
