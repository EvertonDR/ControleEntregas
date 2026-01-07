package com.example.controleentregas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.controleentregas.data.CustoEntity

@Composable
fun CaixaScreen(viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val caixaUiState by viewModel.caixaUiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var custoToDelete by remember { mutableStateOf<CustoEntity?>(null) }

    if (showDeleteDialog && custoToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza de que deseja apagar este custo?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCusto(custoToDelete!!)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Deletar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Caixa") }) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Saldo Atual", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("R$ ${String.format("%.2f", caixaUiState.saldo)}", fontSize = 36.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Entradas", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Text("R$ ${String.format("%.2f", caixaUiState.entradas)}", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Saídas", fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                    Text("R$ ${String.format("%.2f", caixaUiState.saidas)}", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Histórico de Custos", fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(caixaUiState.custos) {
                    CustoItem(custo = it, onDeleteClick = {
                        custoToDelete = it
                        showDeleteDialog = true
                    })
                }
            }
        }
    }
}

@Composable
fun CustoItem(custo: CustoEntity, onDeleteClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = custo.nome, modifier = Modifier.weight(1f))
            Text(text = "R$ ${String.format("%.2f", custo.valor)}", fontWeight = FontWeight.Medium)
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar Custo", tint = Color.Gray)
            }
        }
    }
}
