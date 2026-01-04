package com.example.controleentregas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PagasScreen(
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val pagasUiState by viewModel.pagasUiState.collectAsState()

    if (pagasUiState.entregasPagasPorCliente.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Nenhuma entrega paga encontrada")
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pagasUiState.entregasPagasPorCliente.forEach { (clienteNome, entregas) ->
                item {
                    ClienteSection(clienteNome = clienteNome, entregas = entregas)
                }
            }
        }
    }
}

@Composable
fun ClienteSection(clienteNome: String, entregas: List<EntregaDisplay>) {
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
                text = clienteNome,
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
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                entregas.forEach { entrega ->
                    EntregaPagaItem(entrega = entrega)
                }
            }
        }
    }
}

@Composable
fun EntregaPagaItem(entrega: EntregaDisplay) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Data: ${entrega.data}", fontSize = 14.sp)
                Text(text = "Bairro: ${entrega.bairroNome}", fontSize = 14.sp)
            }
            Text(
                text = "R$ ${String.format("%.2f", entrega.valor)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
