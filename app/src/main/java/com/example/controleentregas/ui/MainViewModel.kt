package com.example.controleentregas.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controleentregas.data.BairroEntity
import com.example.controleentregas.data.ClienteEntity
import com.example.controleentregas.data.EntregasRepository
import com.example.controleentregas.data.EntregaEntity
import com.example.controleentregas.util.PdfExporter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val entregasRepository: EntregasRepository) : ViewModel() {

    private val _filtroData = MutableStateFlow<String?>(null)
    val filtroData: StateFlow<String?> = _filtroData.asStateFlow()

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
                    valor = entrega.valor,
                    data = entrega.data,
                    pago = entrega.pago,
                    realizada = entrega.realizada,
                    originalEntrega = entrega
                )
            }
        }
    }

    val mainUiState: StateFlow<MainUiState> = 
        filtroData.flatMapLatest { data ->
            val entregasFlow = if (data == null) {
                entregasRepository.getEntregasEmAberto()
            } else {
                entregasRepository.getEntregasEmAbertoPorData(data)
            }

            val totalFlow = if (data == null) {
                entregasRepository.getTotalEmAberto()
            } else {
                entregasRepository.getTotalEmAbertoPorData(data)
            }

            getEntregasDisplayFlow(entregasFlow).combine(totalFlow) { entregas, total ->
                MainUiState(entregas = entregas, total = total ?: 0.0)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MainUiState()
        )

    val pagasUiState: StateFlow<PagasUiState> = 
        getEntregasDisplayFlow(entregasRepository.getEntregasPagas())
            .map { entregasDisplay ->
                val entregasAgrupadas = entregasDisplay.groupBy { it.clienteNome }
                PagasUiState(entregasPagasPorCliente = entregasAgrupadas)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = PagasUiState()
            )

    val realizadasUiState: StateFlow<RealizadasUiState> = 
        getEntregasDisplayFlow(entregasRepository.getEntregasRealizadas())
            .map { entregasDisplay ->
                val entregasPorCliente = entregasDisplay.groupBy { it.clienteNome }
                val resumoClientes = entregasPorCliente.mapValues { (_, entregas) ->
                    val totalNaoPago = entregas.count { !it.pago }
                    val valorNaoPago = entregas.filter { !it.pago }.sumOf { it.valor }
                    ClienteResumo(totalNaoPago, valorNaoPago)
                }
                RealizadasUiState(entregasPorCliente, resumoClientes)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = RealizadasUiState()
            )

    fun marcarComoPaga(entrega: EntregaEntity) {
        viewModelScope.launch {
            entregasRepository.updateEntrega(entrega.copy(pago = true))
        }
    }
    
    fun marcarComoRealizada(entrega: EntregaEntity) {
        viewModelScope.launch {
            entregasRepository.updateEntrega(entrega.copy(realizada = true))
        }
    }

    fun setFiltroData(data: Date?) {
        if (data == null) {
            _filtroData.value = null
        } else {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            _filtroData.value = sdf.format(data)
        }
    }
    
    fun limparFiltro() {
        _filtroData.value = null
    }

    fun exportarPdf(context: Context) {
        viewModelScope.launch {
            // TODO: Ajustar exportação de PDF para usar EntregaDisplay
//            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
//            val nomeArquivo = "RelatorioEntregas_${sdf.format(Date())}"
//            val entregasAtuais = mainUiState.value.entregas
//            PdfExporter.export(context, entregasAtuais, nomeArquivo)
        }
    }

    fun exportarResumoCliente(context: Context, clienteNome: String) {
        realizadasUiState.value.resumoPorCliente[clienteNome]?.let { resumo ->
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val nomeArquivo = "Resumo_${clienteNome}_${sdf.format(Date())}"
            // TODO: Criar ou ajustar um método em PdfExporter para este resumo
            // PdfExporter.exportResumo(context, clienteNome, resumo, nomeArquivo)
        }
    }

    fun inserirEntrega(clienteId: Int, bairroId: Int, valor: Double, data: String) {
        viewModelScope.launch {
            val novaEntrega = EntregaEntity(
                clienteId = clienteId,
                bairroId = bairroId,
                valor = valor,
                data = data,
                pago = false,
                realizada = false
            )
            entregasRepository.insertEntrega(novaEntrega)
        }
    }
    
    fun inserirCliente(nome: String) {
        viewModelScope.launch {
            val novoCliente = ClienteEntity(nome = nome)
            entregasRepository.insertCliente(novoCliente)
        }
    }

    fun inserirBairro(nome: String, valor: Double) {
        viewModelScope.launch {
            val novoBairro = BairroEntity(nome = nome, valorEntrega = valor)
            entregasRepository.insertBairro(novoBairro)
        }
    }
}

data class MainUiState(
    val entregas: List<EntregaDisplay> = listOf(),
    val total: Double = 0.0
)

data class PagasUiState(
    val entregasPagasPorCliente: Map<String, List<EntregaDisplay>> = emptyMap()
)

data class RealizadasUiState(
    val entregasPorCliente: Map<String, List<EntregaDisplay>> = emptyMap(),
    val resumoPorCliente: Map<String, ClienteResumo> = emptyMap()
)

data class ClienteResumo(
    val totalEntregasNaoPagas: Int,
    val valorTotalNaoPago: Double
)
