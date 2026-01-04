
package com.example.controleentregas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBairroScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var valor by rememberSaveable { mutableStateOf("") }
    var selectedCidade by rememberSaveable { mutableStateOf<String?>(null) }
    val cidades = listOf("Bayeux", "João Pessoa", "Santa Rita")
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do Bairro") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
            
            DropdownMenu(
                label = "Cidade",
                items = cidades,
                selectedItem = selectedCidade,
                onItemSelected = { selectedCidade = it },
                itemLabel = { it }
            )
            
            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor da Entrega") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = {
                    if (selectedCidade != null) {
                        viewModel.inserirBairro(nome, valor.toDouble(), selectedCidade!!)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nome.isNotBlank() && valor.isNotBlank() && selectedCidade != null
            ) {
                Text("Salvar")
            }
        }
    }
}
