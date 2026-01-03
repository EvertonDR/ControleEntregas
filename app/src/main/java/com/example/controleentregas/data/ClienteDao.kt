package com.example.controleentregas.data

@Dao
interface ClienteDao {

    @Insert
    fun inserir(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes")
    fun listar(): List<ClienteEntity>
}
