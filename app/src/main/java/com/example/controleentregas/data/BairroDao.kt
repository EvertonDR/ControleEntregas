package com.example.controleentregas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BairroDao {

    @Insert
    suspend fun inserir(bairro: BairroEntity)

    @Query("SELECT * FROM bairros ORDER BY nome ASC")
    fun listar(): Flow<List<BairroEntity>>
}
