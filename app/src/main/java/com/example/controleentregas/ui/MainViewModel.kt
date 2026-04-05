package com.example.controleentregas.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controleentregas.data.BackupData
import com.example.controleentregas.data.BairroEntity
import com.example.controleentregas.data.ClienteEntity
import com.example.controleentregas.data.CustoEntity
import com.example.controleentregas.data.EntregasRepository
import com.example.controleentregas.data.EntregaEntity
import com.example.controleentregas.util.JsonExporter
import com.example.controleentregas.util.PdfExporter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val entregasRepository: EntregasRepository) : ViewModel() {

    private val clientesFlow = entregasRepository.getClientes()
    private val bairrosFlow = entregasRepository.getBairros()

    private val _filtroData = MutableStateFlow<String?>(null)
    val filtroData = _filtroData.asStateFlow()

    private val _filtroDataPagas = MutableStateFlow<String?>(null)
    val filtroDataPagas = _filtroDataPagas.asStateFlow()

    private val _filtroDataRealizadas = MutableStateFlow<String?>(null)
    val filtroDataRealizadas = _filtroDataRealizadas.asStateFlow()

    val mainUiState: StateFlow<MainUiState> = _filtroData.flatMapLatest { data ->
        val eFlow = if (data == null) entregasRepository.getEntregasEmAberto() else entregasRepository.getEntregasEmAbertoPorData(data)
        val tFlow = if (data == null) entregasRepository.getTotalEmAberto() else entregasRepository.getTotalEmAbertoPorData(data)
        combine(eFlow, tFlow, clientesFlow, bairrosFlow) { e, t, c, b -> MainUiState(transform(e, c, b), t ?: 0.0) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState())

    val pagasUiState: StateFlow<PagasUiState> = _filtroDataPagas.flatMapLatest { data ->
        val eFlow = if (data == null) entregasRepository.getEntregasPagas() else entregasRepository.getEntregasPagasPorData(data)
        val tFlow = if (data == null) entregasRepository.getTotalPago() else entregasRepository.getTotalPagoPorData(data)
        combine(eFlow, tFlow, clientesFlow, bairrosFlow) { e, t, c, b -> PagasUiState(transform(e, c, b).groupBy { it.clienteNome }, t ?: 0.0) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagasUiState())

    val realizadasUiState: StateFlow<RealizadasUiState> = _filtroDataRealizadas.flatMapLatest { data ->
        val eFlow = if (data == null) entregasRepository.getEntregasRealizadas() else entregasRepository.getEntregasRealizadasPorData(data)
        val tFlow = if (data == null) entregasRepository.getTotalRealizadas() else entregasRepository.getTotalRealizadasPorData(data)
        combine(eFlow, tFlow, clientesFlow, bairrosFlow) { e, t, c, b ->
            val list = transform(e, c, b)
            val agrupadas = list.groupBy { it.clienteNome }
            val resumos = agrupadas.mapValues { (_, sub) ->
                val p = sub.filter { it.pago }; val np = sub.filter { !it.pago }
                ClienteResumo(sub.size, p.size, np.size, p.sumOf { it.valor }, np.sumOf { it.valor })
            }
            RealizadasUiState(agrupadas, resumos, t ?: 0.0)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RealizadasUiState())

    val naoPagasUiState: StateFlow<NaoPagasUiState> = combine(entregasRepository.getEntregasNaoPagas(), entregasRepository.getTotalNaoPago(), clientesFlow, bairrosFlow) { e, t, c, b ->
        val list = transform(e, c, b); val agrupadas = list.groupBy { it.clienteNome }
        NaoPagasUiState(agrupadas, agrupadas.mapValues { it.value.sumOf { ent -> ent.valor } }, t ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NaoPagasUiState())

    val caixaUiState: StateFlow<CaixaUiState> = combine(entregasRepository.getTotalPago(), entregasRepository.getTotalCustos(), entregasRepository.getCustos()) { t, tc, c ->
        CaixaUiState((t ?: 0.0) - (tc ?: 0.0), t ?: 0.0, tc ?: 0.0, c)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CaixaUiState())

    fun setFiltroData(date: Date?) { _filtroData.value = date?.format() }
    fun setFiltroDataPagas(date: Date?) { _filtroDataPagas.value = date?.format() }
    fun setFiltroDataRealizadas(date: Date?) { _filtroDataRealizadas.value = date?.format() }
    private fun Date.format() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this)

    fun togglePagoStatus(e: EntregaEntity) = viewModelScope.launch { entregasRepository.updateEntrega(e.copy(pago = !e.pago)) }
    fun toggleRealizadaStatus(e: EntregaEntity) = viewModelScope.launch { entregasRepository.updateEntrega(e.copy(realizada = !e.realizada)) }
    fun deleteEntrega(e: EntregaEntity) = viewModelScope.launch { entregasRepository.deleteEntrega(e) }
    fun inserirCusto(n: String, v: Double) = viewModelScope.launch { entregasRepository.insertCusto(CustoEntity(nome = n, valor = v)) }
    fun deleteCusto(c: CustoEntity) = viewModelScope.launch { entregasRepository.deleteCusto(c) }

    fun exportarBackupTotal(context: Context) = viewModelScope.launch {
        val toutes = entregasRepository.getTodasAsEntregas().first()
        val c = entregasRepository.getClientes().first(); val b = entregasRepository.getBairros().first()
        val nome = "ERDelivery_Backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}"
        PdfExporter.exportarBackupTotal(context, transform(toutes, c, b), nome)
        JsonExporter.export(context, BackupData(c, b, toutes), nome)
    }

    fun importarBackup(context: Context, uri: Uri) = viewModelScope.launch {
        val backup = JsonExporter.import(context, uri)
        if (backup != null) entregasRepository.restaurarBackup(backup)
    }

    fun exportarResumoCliente(context: Context, nome: String) = viewModelScope.launch {
        val state = realizadasUiState.value; val res = state.resumoPorCliente[nome]; val ent = state.entregasPorCliente[nome]
        if (res != null && ent != null) PdfExporter.exportarResumoCliente(context, nome, res, ent, "Resumo_${nome.replace(" ", "_")}")
    }

    fun exportarNaoPagasCliente(context: Context, nome: String) = viewModelScope.launch {
        naoPagasUiState.value.entregasNaoPagasPorCliente[nome]?.let { PdfExporter.exportarNaoPagasCliente(context, nome, it, "Cobranca_${nome.replace(" ", "_")}") }
    }

    fun inserirEntrega(cId: Int, bId: Int, v: Double, d: String, cid: String) = viewModelScope.launch {
        entregasRepository.insertEntrega(EntregaEntity(clienteId = cId, bairroId = bId, valor = v, data = d, pago = false, realizada = false, cidade = cid))
    }
    fun inserirCliente(n: String) = viewModelScope.launch { entregasRepository.insertCliente(ClienteEntity(nome = n)) }
    fun inserirBairro(n: String, v: Double, c: String) = viewModelScope.launch { entregasRepository.insertBairro(BairroEntity(nome = n, valorEntrega = v, cidade = c)) }

    val clientes: StateFlow<List<ClienteEntity>> = entregasRepository.getClientes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val bairros: StateFlow<List<BairroEntity>> = entregasRepository.getBairros().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun transform(e: List<EntregaEntity>, c: List<ClienteEntity>, b: List<BairroEntity>): List<EntregaDisplay> {
        val cMap = c.associateBy { it.id }; val bMap = b.associateBy { it.id }
        return e.map { EntregaDisplay(it.id, cMap[it.clienteId]?.nome ?: "Desconhecido", bMap[it.bairroId]?.nome ?: "Desconhecido", it.cidade, it.valor, it.data, it.pago, it.realizada, it) }
    }
}

data class MainUiState(val entregas: List<EntregaDisplay> = listOf(), val total: Double = 0.0)
data class PagasUiState(val entregasPagasPorCliente: Map<String, List<EntregaDisplay>> = emptyMap(), val totalPago: Double = 0.0)
data class NaoPagasUiState(val entregasNaoPagasPorCliente: Map<String, List<EntregaDisplay>> = emptyMap(), val subtotalPorCliente: Map<String, Double> = emptyMap(), val totalNaoPago: Double = 0.0)
data class RealizadasUiState(val entregasPorCliente: Map<String, List<EntregaDisplay>> = emptyMap(), val resumoPorCliente: Map<String, ClienteResumo> = emptyMap(), val totalRealizadas: Double = 0.0)
data class CaixaUiState(val saldo: Double = 0.0, val entradas: Double = 0.0, val saidas: Double = 0.0, val custos: List<CustoEntity> = emptyList())
data class ClienteResumo(val totalEntregasRealizadas: Int, val totalEntregasPagas: Int, val totalEntregasNaoPagas: Int, val valorTotalPago: Double, val valorTotalNaoPago: Double)
