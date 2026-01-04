package com.example.controleentregas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntregaDao {

    @Insert
    suspend fun inserir(entrega: EntregaEntity)

    @Update
    suspend fun update(entrega: EntregaEntity)

    @Query("SELECT * FROM entregas WHERE pago = 0 ORDER BY data DESC")
    fun listarEmAberto(): Flow<List<EntregaEntity>>

    @Query("SELECT * FROM entregas WHERE pago = 0 AND data = :data ORDER BY data DESC")
    fun listarEmAbertoPorData(data: String): Flow<List<EntregaEntity>>

    @Query("SELECT SUM(valor) FROM entregas WHERE pago = 0")
    fun totalEmAberto(): Flow<Double?>

    @Query("SELECT SUM(valor) FROM entregas WHERE pago = 0 AND data = :data")
    fun totalEmAbertoPorData(data: String): Flow<Double?>
    
    @Query("SELECT * FROM entregas WHERE pago = 1 ORDER BY data DESC")
    fun listarPagas(): Flow<List<EntregaEntity>>

    @Query("SELECT * FROM entregas WHERE realizada = 1 ORDER BY data DESC")
    fun listarRealizadas(): Flow<List<EntregaEntity>>
}
