package com.example.controleentregas.data

import kotlinx.coroutines.flow.Flow

class EntregasRepository(
    private val clienteDao: ClienteDao,
    private val bairroDao: BairroDao,
    private val entregaDao: EntregaDao
) {

    // Cliente funcs
    fun getClientes(): Flow<List<ClienteEntity>> = clienteDao.listar()

    suspend fun insertCliente(cliente: ClienteEntity) {
        clienteDao.inserir(cliente)
    }

    // Bairro funcs
    fun getBairros(): Flow<List<BairroEntity>> = bairroDao.listar()

    suspend fun insertBairro(bairro: BairroEntity) {
        bairroDao.inserir(bairro)
    }

    // Entrega funcs
    fun getEntregasEmAberto(): Flow<List<EntregaEntity>> = entregaDao.listarEmAberto()
    
    fun getEntregasEmAbertoPorData(data: String): Flow<List<EntregaEntity>> = entregaDao.listarEmAbertoPorData(data)

    fun getTotalEmAberto(): Flow<Double?> = entregaDao.totalEmAberto()
    
    fun getTotalEmAbertoPorData(data: String): Flow<Double?> = entregaDao.totalEmAbertoPorData(data)

    suspend fun insertEntrega(entrega: EntregaEntity) {
        entregaDao.inserir(entrega)
    }

    suspend fun updateEntrega(entrega: EntregaEntity) {
        entregaDao.update(entrega)
    }
    
    fun getEntregasPagas(): Flow<List<EntregaEntity>> = entregaDao.listarPagas()

    fun getEntregasRealizadas(): Flow<List<EntregaEntity>> = entregaDao.listarRealizadas()

    fun getEntregasNaoPagas(): Flow<List<EntregaEntity>> = entregaDao.listarNaoPagas()
}