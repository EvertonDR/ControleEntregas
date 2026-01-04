package com.example.controleentregas.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    val todosOsBairros by viewModel.bairros.collectAsState()
    val cidades = listOf("Bayeux", "João Pessoa", "Santa Rita")

    var selectedClienteId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedBairroId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedCidade by rememberSaveable { mutableStateOf<String?>(null) }

    val bairrosFiltrados = todosOsBairros.filter { it.cidade == selectedCidade }

    // Reset bairro selection when cidade changes
    LaunchedEffect(selectedCidade) {
        selectedBairroId = null
    }

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var data by rememberSaveable { mutableStateOf(sdf.format(Date())) }

    val selectedCliente = clientes.find { it.id == selectedClienteId }
    val selectedBairro = bairrosFiltrados.find { it.id == selectedBairroId }

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
            
            DropdownMenu(label = "Cliente", items = clientes, selectedItem = selectedCliente, onItemSelected = { selectedClienteId = it.id }, itemLabel = { it.nome })

            DropdownMenu(
                label = "Cidade", 
                items = cidades, 
                selectedItem = selectedCidade, 
                onItemSelected = { selectedCidade = it }, 
                itemLabel = { it }
            )

            DropdownMenu(label = "Bairro", items = bairrosFiltrados, selectedItem = selectedBairro, onItemSelected = { selectedBairroId = it.id }, itemLabel = { "${it.nome} - R$ ${it.valorEntrega}" })
            
            OutlinedTextField(
                value = data,
                onValueChange = { data = it },
                label = { Text("Data") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (selectedCliente != null && selectedBairro != null && selectedCidade != null) {
                        viewModel.inserirEntrega(selectedCliente.id, selectedBairro.id, selectedBairro.valorEntrega, data, selectedCidade!!)
                        navController.popBackStack() // Go back to main screen
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCliente != null && selectedBairro != null && selectedCidade != null
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
