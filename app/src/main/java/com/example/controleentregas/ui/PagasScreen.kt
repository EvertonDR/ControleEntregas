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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun PagasScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val pagasUiState by viewModel.pagasUiState.collectAsState()
    val filtroData by viewModel.filtroDataPagas.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }
    var showAddCustoDialog by remember { mutableStateOf(false) }

    if (showAddCustoDialog) {
        AddCustoDialog(
            onDismissRequest = { showAddCustoDialog = false },
            onConfirm = {
                viewModel.inserirCusto(it.nome, it.valor)
                showAddCustoDialog = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isExpanded) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("caixa_screen")
                            isExpanded = false
                        },
                    ) {
                        Icon(Icons.Default.Money, "Caixa")
                    }
                    FloatingActionButton(
                        onClick = {
                            showAddCustoDialog = true
                            isExpanded = false
                        },
                    ) {
                        Icon(Icons.Default.MoneyOff, "Adicionar Custo")
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
    ) {
        Column(modifier = Modifier.padding(it).padding(16.dp)) {
            Header(
                defaultTitle = "Total Recebido",
                total = pagasUiState.totalPago,
                filtro = filtroData,
                onFilterClick = viewModel::setFiltroDataPagas,
                onClearFilter = viewModel::limparFiltroPagas,
                onBackupClick = null
            )
            Spacer(modifier = Modifier.padding(8.dp))

            if (pagasUiState.entregasPagasPorCliente.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Nenhuma entrega paga encontrada")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pagasUiState.entregasPagasPorCliente.forEach { (clienteNome, entregas) ->
                        item {
                            ClientePagasSection(clienteNome = clienteNome, entregas = entregas, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddCustoDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (CustoTemp) -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var valor by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Adicionar Novo Custo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do custo") }
                )
                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("Valor do custo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val valorDouble = valor.toDoubleOrNull()
                    if(nome.isNotBlank() && valorDouble != null) {
                        onConfirm(CustoTemp(nome, valorDouble))
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}

data class CustoTemp(val nome: String, val valor: Double)

@Composable
fun ClientePagasSection(clienteNome: String, entregas: List<EntregaDisplay>, viewModel: MainViewModel) {
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
                text = "$clienteNome (${entregas.size})",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge
            )
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
                    EntregaPagaItem(
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
fun EntregaPagaItem(
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
