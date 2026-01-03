package com.example.controleentregas.data

@Entity(tableName "bairros")
data class BairroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val valorEntrega: Double
)