package com.example.controleentregas.data

import kotlinx.serialization.Serializable

@Serializable
data class BackupData(
    val clientes: List<ClienteEntity>,
    val bairros: List<BairroEntity>,
    val entregas: List<EntregaEntity>
)
