package com.example.controleentregas.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "entregas",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"]
        ),
        ForeignKey(
            entity = BairroEntity::class,
            parentColumns = ["id"],
            childColumns = ["bairroId"]
        )
    ]
)
data class EntregaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clienteId: Int = 0,
    val bairroId: Int = 0,
    val valor: Double,
    val data: String,
    val pago: Boolean,
    val realizada: Boolean = false,
    val cidade: String = ""
)
