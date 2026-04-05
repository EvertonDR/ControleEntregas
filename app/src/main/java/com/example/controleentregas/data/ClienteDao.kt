package com.example.controleentregas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(cliente: ClienteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirLista(clientes: List<ClienteEntity>)

    @Query("DELETE FROM clientes")
    suspend fun limparTudo()

    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun listar(): Flow<List<ClienteEntity>>
}
