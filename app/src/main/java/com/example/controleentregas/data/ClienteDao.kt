package com.example.controleentregas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Insert
    suspend fun inserir(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun listar(): Flow<List<ClienteEntity>>
}
