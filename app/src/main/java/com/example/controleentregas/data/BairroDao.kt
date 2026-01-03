package com.example.controleentregas.data

@Dao
interface BairroDao {

    @Insert
    fun inserir(bairro: BairroEntity)

    @Query("SELECT * FROM bairros")
    fun listar(): List<BairroEntity>
}