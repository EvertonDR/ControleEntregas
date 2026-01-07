package com.example.controleentregas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustoDao {
    @Insert
    suspend fun inserir(custo: CustoEntity)

    @Delete
    suspend fun delete(custo: CustoEntity)

    @Query("SELECT * FROM custos ORDER BY id DESC")
    fun listar(): Flow<List<CustoEntity>>

    @Query("SELECT SUM(valor) FROM custos")
    fun totalCustos(): Flow<Double?>
}
