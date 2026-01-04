package com.example.controleentregas.ui

import com.example.controleentregas.data.EntregaEntity

data class EntregaDisplay(
    val id: Int,
    val clienteNome: String,
    val bairroNome: String,
    val cidade: String,
    val valor: Double,
    val data: String,
    val pago: Boolean,
    val realizada: Boolean,
    // Mantemos a entidade original para realizar updates no banco de dados
    val originalEntrega: EntregaEntity
)
