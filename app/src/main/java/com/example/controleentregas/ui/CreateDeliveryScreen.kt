package com.example.controleentregas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.controleentregas.data.BairroEntity
import com.example.controleentregas.data.ClienteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeliveryScreen(navController: NavController, viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val clientes by viewModel.clientes.collectAsState()
    val bairros by viewModel.bairros.collectAsState()

    var selectedCliente by remember { mutableStateOf<ClienteEntity?>(null) }
    var selectedBairro by remember { mutableStateOf<BairroEntity?>(null) }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var data by remember { mutableStateOf(sdf.format(Date())) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nova Entrega") }) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            DropdownMenu(label = "Cliente", items = clientes, selectedItem = selectedCliente, onItemSelected = { selectedCliente = it }, itemLabel = { it.nome })

            DropdownMenu(label = "Bairro", items = bairros, selectedItem = selectedBairro, onItemSelected = { selectedBairro = it }, itemLabel = { "${it.nome} - R$ ${it.valorEntrega}" })
            
            OutlinedTextField(
                value = data,
                onValueChange = { data = it },
                label = { Text("Data") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val cliente = selectedCliente
                    val bairro = selectedBairro
                    if (cliente != null && bairro != null) {
                        viewModel.inserirEntrega(cliente.id, bairro.id, bairro.valorEntrega, data)
                        navController.popBackStack() // Go back to main screen
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCliente != null && selectedBairro != null
            ) {
                Text("Salvar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenu(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedItem?.let(itemLabel) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}