package com.example.controleentregas.model

data class Entrega(
    val id: Int = 0,
    val clienteId: Int,
    val bairroId: Int,
    val valor: Double,
    val data: String,
    val pago: Boolean
)
