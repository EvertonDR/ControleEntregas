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

            entregasFlow.combine(totalFlow) { entregas, total ->
                MainUiState(entregas = entregas, total = total ?: 0.0)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MainUiState()
        )

    val clientes: StateFlow<List<ClienteEntity>> = entregasRepository.getClientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bairros: StateFlow<List<BairroEntity>> = entregasRepository.getBairros()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun marcarComoPaga(entrega: EntregaEntity) {
        viewModelScope.launch {
            entregasRepository.updateEntrega(entrega.copy(pago = true))
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
            val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val nomeArquivo = "RelatorioEntregas_${sdf.format(Date())}"
            val entregasAtuais = mainUiState.value.entregas
            PdfExporter.export(context, entregasAtuais, nomeArquivo)
        }
    }

    fun inserirEntrega(clienteId: Int, bairroId: Int, valor: Double, data: String) {
        viewModelScope.launch {
            val novaEntrega = EntregaEntity(
                clienteId = clienteId,
                bairroId = bairroId,
                valor = valor,
                data = data,
                pago = false
            )
            entregasRepository.insertEntrega(novaEntrega)
        }
    }
}

data class MainUiState(
    val entregas: List<EntregaEntity> = listOf(),
    val total: Double = 0.0
)
