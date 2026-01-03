package com.example.controleentregas.data

@Dao
interface EntregaDao {

    @insert
    fun inserir(entrega: EntregaEntity)

    @Query("SELECT * FROM entregas WHERE pago = 0")
    fun listarEmAberto(): List<EntregaEntity>

    @Query("""
        SELECT SUM(valor)
        FROM entregas
        WHERE pago = 0
    """)
    fun totalEmAberto(): Double
}