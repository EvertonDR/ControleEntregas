package com.example.controleentregas.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.controleentregas.ControleEntregasApplication
import com.example.controleentregas.data.EntregasRepository

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MainViewModel(
                entregasApplication().container.entregasRepository
            )
        }
    }
}

fun CreationExtras.entregasApplication(): ControleEntregasApplication = 
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ControleEntregasApplication