package com.example.controleentregas.ui

import android.content.Context
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

    // Filtros de Data para cada aba
    private val _filtroData = MutableStateFlow<String?>(null)
    val filtroData: StateFlow<String?> = _filtroData.asStateFlow()

    private val _filtroDataPagas = MutableStateFlow<String?>(null)
    val filtroDataPagas: StateFlow<String?> = _filtroDataPagas.asStateFlow()

    private val _filtroDataRealizadas = MutableStateFlow<String?>(null)
    val filtroDataRealizadas: StateFlow<String?> = _filtroDataRealizadas.asStateFlow()

    // Auxiliar para formatar entregas com nomes de clientes e bairros
    private fun getEntregasDisplayFlow(entregasFlow: Flow<List<EntregaEntity>>): Flow<List<EntregaDisplay>> {
        return combine(
            entregasFlow,
            entregasRepository.getClientes(),
            entregasRepository.getBairros()
        ) { entregas, clientes, bairros ->
            val clientesMap = clientes.associateBy { it.id }
            val bairrosMap = bairros.associateBy { it.id }
            entregas.map { entrega ->
                EntregaDisplay(
                    id = entrega.id,
                    clienteNome = clientesMap[entrega.clienteId]?.nome ?: "Desconhecido",
                    bairroNome = bairrosMap[entrega.bairroId]?.nome ?: "Desconhecido",
                    cidade = entrega.cidade,
                    valor = entrega.valor,
                    data = entrega.data,
                    pago = entrega.pago,
                    realizada = entrega.realizada,
                    originalEntrega = entrega
                )
            }
        }
    }

    // Estado da aba "Em Aberto"
    val mainUiState: StateFlow<MainUiState> = 
        _filtroData.flatMapLatest { data ->
            val flow = if (data == null) entregasRepository.getEntregasEmAberto() else entregasRepository.getEntregasEmAbertoPorData(data)
            val totalFlow = if (data == null) entregasRepository.getTotalEmAberto() else entregasRepository.getTotalEmAbertoPorData(data)
            
            combine(getEntregasDisplayFlow(flow), totalFlow) { list, total ->
                MainUiState(entregas = list, total = total ?: 0.0)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState())

    // Estado da aba "Pagas"
    val pagasUiState: StateFlow<PagasUiState> = 
        _filtroDataPagas.flatMapLatest { data ->
            val flow = if (data == null) entregasRepository.getEntregasPagas() else entregasRepository.getEntregasPagasPorData(data)
            val totalFlow = if (data == null) entregasRepository.getTotalPago() else entregasRepository.getTotalPagoPorData(data)
            
            combine(getEntregasDisplayFlow(flow), totalFlow) { list, total ->
                PagasUiState(entregasPagasPorCliente = list.groupBy { it.clienteNome }, totalPago = total ?: 0.0)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagasUiState())
    
    // Estado da aba "Não Pagas"
    val naoPagasUiState: StateFlow<NaoPagasUiState> = 
        combine(
            getEntregasDisplayFlow(entregasRepository.getEntregasNaoPagas()),
            entregasRepository.getTotalNaoPago()
        ) { list, total ->
            val agrupadas = list.groupBy { it.clienteNome }
            NaoPagasUiState(
                entregasNaoPagasPorCliente = agrupadas,
                subtotalPorCliente = agrupadas.mapValues { it.value.sumOf { e -> e.valor } },
                totalNaoPago = total ?: 0.0
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NaoPagasUiState())

    // Estado da aba "Realizadas"
    val realizadasUiState: StateFlow<RealizadasUiState> = 
        _filtroDataRealizadas.flatMapLatest { data ->
            val flow = if (data == null) entregasRepository.getEntregasRealizadas() else entregasRepository.getEntregasRealizadasPorData(data)
            val totalFlow = if (data == null) entregasRepository.getTotalRealizadas() else entregasRepository.getTotalRealizadasPorData(data)
            
            combine(getEntregasDisplayFlow(flow), totalFlow) { list, total ->
                val agrupadas = list.groupBy { it.clienteNome }
                val resumos = agrupadas.mapValues { (_, subList) ->
                    val pagas = subList.filter { it.pago }
                    val naoPagas = subList.filter { !it.pago }
                    ClienteResumo(
                        totalEntregasRealizadas = subList.size,
                        totalEntregasPagas = pagas.size,
                        totalEntregasNaoPagas = naoPagas.size,
                        valorTotalPago = pagas.sumOf { it.valor },
                        valorTotalNaoPago = naoPagas.sumOf { it.valor }
                    )
                }
                RealizadasUiState(agrupadas, resumos, total ?: 0.0)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RealizadasUiState())

    // Estado do "Caixa"
    val caixaUiState: StateFlow<CaixaUiState> = 
        combine(
            entregasRepository.getTotalPago(),
            entregasRepository.getTotalCustos(),
            entregasRepository.getCustos()
        ) { totalPago, totalCustos, custos ->
            val entradas = totalPago ?: 0.0
            val saidas = totalCustos ?: 0.0
            CaixaUiState(entradas - saidas, entradas, saidas, custos)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CaixaUiState())

    val clientes: StateFlow<List<ClienteEntity>> = entregasRepository.getClientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bairros: StateFlow<List<BairroEntity>> = entregasRepository.getBairros()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Funções de Status e Ações
    fun togglePagoStatus(entrega: EntregaEntity) = viewModelScope.launch { entregasRepository.updateEntrega(entrega.copy(pago = !entrega.pago)) }
    fun toggleRealizadaStatus(entrega: EntregaEntity) = viewModelScope.launch { entregasRepository.updateEntrega(entrega.copy(realizada = !entrega.realizada)) }
    fun deleteEntrega(entrega: EntregaEntity) = viewModelScope.launch { entregasRepository.deleteEntrega(entrega) }
    fun inserirCusto(nome: String, valor: Double) = viewModelScope.launch { entregasRepository.insertCusto(CustoEntity(nome = nome, valor = valor)) }
    fun deleteCusto(custo: CustoEntity) = viewModelScope.launch { entregasRepository.deleteCusto(custo) }

    // Funções de Controle de Filtros
    fun setFiltroData(date: Date?) { _filtroData.value = date?.toFormattedString() }
    fun limparFiltro() { _filtroData.value = null }
    
    fun setFiltroDataPagas(date: Date?) { _filtroDataPagas.value = date?.toFormattedString() }
    fun limparFiltroPagas() { _filtroDataPagas.value = null }

    fun setFiltroDataRealizadas(date: Date?) { _filtroDataRealizadas.value = date?.toFormattedString() }
    fun limparFiltroRealizadas() { _filtroDataRealizadas.value = null }

    private fun Date.toFormattedString(): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this)

    // Exportações
    fun exportarBackupTotal(context: Context) = viewModelScope.launch {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val nomeArquivo = "ERDelivery_Backup_${sdf.format(Date())}"
        
        val todasEntregas = entregasRepository.getTodasAsEntregas().first()
        val displayEntregas = getEntregasDisplayFlow(flowOf(todasEntregas)).first()
        
        PdfExporter.exportarBackupTotal(context, displayEntregas, nomeArquivo)
        JsonExporter.export(context, BackupData(entregasRepository.getClientes().first(), entregasRepository.getBairros().first(), todasEntregas), nomeArquivo)
    }

    fun exportarResumoCliente(context: Context, clienteNome: String) = viewModelScope.launch {
        val state = realizadasUiState.value
        val resumo = state.resumoPorCliente[clienteNome]
        val entregas = state.entregasPorCliente[clienteNome]

        if (resumo != null && entregas != null) {
            val nomeArquivo = "Resumo_${clienteNome.replace(" ", "_")}_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}"
            PdfExporter.exportarResumoCliente(context, clienteNome, resumo, entregas, nomeArquivo)
        }
    }

    fun exportarNaoPagasCliente(context: Context, clienteNome: String) = viewModelScope.launch {
        naoPagasUiState.value.entregasNaoPagasPorCliente[clienteNome]?.let { entregas ->
            val nomeArquivo = "Cobranca_${clienteNome.replace(" ", "_")}_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}"
            PdfExporter.exportarNaoPagasCliente(context, clienteNome, entregas, nomeArquivo)
        }
    }

    // Inserções
    fun inserirEntrega(cId: Int, bId: Int, valEnt: Double, dt: String, cid: String) = viewModelScope.launch {
        entregasRepository.insertEntrega(EntregaEntity(clienteId = cId, bairroId = bId, valor = valEnt, data = dt, pago = false, realizada = false, cidade = cid))
    }
    fun inserirCliente(n: String) = viewModelScope.launch { entregasRepository.insertCliente(ClienteEntity(nome = n)) }
    fun inserirBairro(n: String, v: Double, c: String) = viewModelScope.launch { entregasRepository.insertBairro(BairroEntity(nome = n, valorEntrega = v, cidade = c)) }
}

data class MainUiState(val entregas: List<EntregaDisplay> = listOf(), val total: Double = 0.0)
data class PagasUiState(val entregasPagasPorCliente: Map<String, List<EntregaDisplay>> = emptyMap(), val totalPago: Double = 0.0)
data class NaoPagasUiState(val entregasNaoPagasPorCliente: Map<String, List<EntregaDisplay>> = emptyMap(), val subtotalPorCliente: Map<String, Double> = emptyMap(), val totalNaoPago: Double = 0.0)
data class RealizadasUiState(val entregasPorCliente: Map<String, List<EntregaDisplay>> = emptyMap(), val resumoPorCliente: Map<String, ClienteResumo> = emptyMap(), val totalRealizadas: Double = 0.0)
data class CaixaUiState(val saldo: Double = 0.0, val entradas: Double = 0.0, val saidas: Double = 0.0, val custos: List<CustoEntity> = emptyList())
data class ClienteResumo(val totalEntregasRealizadas: Int, val totalEntregasPagas: Int, val totalEntregasNaoPagas: Int, val valorTotalPago: Double, val valorTotalNaoPago: Double)
