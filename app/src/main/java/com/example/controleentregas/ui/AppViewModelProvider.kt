package com.example.controleentregas.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.controleentregas.ControleEntregasApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MainViewModel(
                controleEntregasApplication().container.entregasRepository
            )
        }

        // Add other ViewModels here
    }
}

fun CreationExtras.controleEntregasApplication(): ControleEntregasApplication {
    return (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ControleEntregasApplication)
}
