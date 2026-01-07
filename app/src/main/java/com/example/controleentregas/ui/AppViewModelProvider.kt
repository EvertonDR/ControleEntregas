package com.example.controleentregas.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.controleentregas.EntregasApplication
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

fun CreationExtras.entregasApplication(): EntregasApplication = 
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EntregasApplication