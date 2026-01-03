package com.example.controleentregas.data

@Entity(tableName = "clientes")
data class ClienteEntity(
    @Primarykey(autoGenerate = true)
    val id: Int = 0,
    val nome: String
)
