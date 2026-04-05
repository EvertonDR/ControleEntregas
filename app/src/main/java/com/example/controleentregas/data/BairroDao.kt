package com.example.controleentregas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BairroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(bairro: BairroEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirLista(bairros: List<BairroEntity>)

    @Query("DELETE FROM bairros")
    suspend fun limparTudo()

    @Query("SELECT * FROM bairros ORDER BY nome ASC")
    fun listar(): Flow<List<BairroEntity>>
}
