package com.example.controleentregas.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntregaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(entrega: EntregaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirLista(entregas: List<EntregaEntity>)

    @Update
    suspend fun update(entrega: EntregaEntity)

    @Delete
    suspend fun delete(entrega: EntregaEntity)

    @Query("DELETE FROM entregas")
    suspend fun limparTudo()

    @Query("SELECT * FROM entregas WHERE realizada = 0 ORDER BY data DESC")
    fun listarEmAberto(): Flow<List<EntregaEntity>>

    @Query("SELECT * FROM entregas WHERE realizada = 0 AND data = :data ORDER BY data DESC")
    fun listarEmAbertoPorData(data: String): Flow<List<EntregaEntity>>

    @Query("SELECT SUM(valor) FROM entregas WHERE realizada = 0")
    fun totalEmAberto(): Flow<Double?>

    @Query("SELECT SUM(valor) FROM entregas WHERE realizada = 0 AND data = :data")
    fun totalEmAbertoPorData(data: String): Flow<Double?>
    
    @Query("SELECT * FROM entregas WHERE pago = 1 ORDER BY data DESC")
    fun listarPagas(): Flow<List<EntregaEntity>>

    @Query("SELECT * FROM entregas WHERE pago = 1 AND data = :data ORDER BY data DESC")
    fun listarPagasPorData(data: String): Flow<List<EntregaEntity>>

    @Query("SELECT SUM(valor) FROM entregas WHERE pago = 1")
    fun totalPago(): Flow<Double?>

    @Query("SELECT SUM(valor) FROM entregas WHERE pago = 1 AND data = :data")
    fun totalPagoPorData(data: String): Flow<Double?>

    @Query("SELECT * FROM entregas WHERE realizada = 1 ORDER BY data DESC")
    fun listarRealizadas(): Flow<List<EntregaEntity>>

    @Query("SELECT * FROM entregas WHERE realizada = 1 AND data = :data ORDER BY data DESC")
    fun listarRealizadasPorData(data: String): Flow<List<EntregaEntity>>

    @Query("SELECT SUM(valor) FROM entregas WHERE realizada = 1")
    fun totalRealizadas(): Flow<Double?>

    @Query("SELECT SUM(valor) FROM entregas WHERE realizada = 1 AND data = :data")
    fun totalRealizadasPorData(data: String): Flow<Double?>

    @Query("SELECT * FROM entregas WHERE pago = 0 ORDER BY data DESC")
    fun listarNaoPagas(): Flow<List<EntregaEntity>>

    @Query("SELECT SUM(valor) FROM entregas WHERE pago = 0")
    fun totalNaoPago(): Flow<Double?>

    @Query("SELECT * FROM entregas ORDER BY clienteId, data DESC")
    fun listarTodas(): Flow<List<EntregaEntity>>
}
