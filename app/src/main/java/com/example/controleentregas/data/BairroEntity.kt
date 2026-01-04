package com.example.controleentregas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bairros")
data class BairroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val valorEntrega: Double,
    val cidade: String
)
