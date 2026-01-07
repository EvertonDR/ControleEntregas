package com.example.controleentregas.data

import kotlinx.coroutines.flow.Flow

class EntregasRepository(
    private val clienteDao: ClienteDao,
    private val bairroDao: BairroDao,
    private val entregaDao: EntregaDao,
    private val custoDao: CustoDao
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

    suspend fun deleteEntrega(entrega: EntregaEntity) {
        entregaDao.delete(entrega)
    }
    
    fun getEntregasPagas(): Flow<List<EntregaEntity>> = entregaDao.listarPagas()

    fun getEntregasPagasPorData(data: String): Flow<List<EntregaEntity>> = entregaDao.listarPagasPorData(data)

    fun getTotalPago(): Flow<Double?> = entregaDao.totalPago()

    fun getTotalPagoPorData(data: String): Flow<Double?> = entregaDao.totalPagoPorData(data)

    fun getEntregasRealizadas(): Flow<List<EntregaEntity>> = entregaDao.listarRealizadas()

    fun getEntregasNaoPagas(): Flow<List<EntregaEntity>> = entregaDao.listarNaoPagas()

    fun getTotalNaoPago(): Flow<Double?> = entregaDao.totalNaoPago()

    fun getTodasAsEntregas(): Flow<List<EntregaEntity>> = entregaDao.listarTodas()

    // Custo funcs
    fun getCustos(): Flow<List<CustoEntity>> = custoDao.listar()

    fun getTotalCustos(): Flow<Double?> = custoDao.totalCustos()

    suspend fun insertCusto(custo: CustoEntity) {
        custoDao.inserir(custo)
    }

    suspend fun deleteCusto(custo: CustoEntity) {
        custoDao.delete(custo)
    }
}