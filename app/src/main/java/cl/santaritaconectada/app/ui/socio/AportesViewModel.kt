package cl.santaritaconectada.app.ui.socio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.santaritaconectada.app.network.ApiService
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.response.ChartData // <-- Se importa el modelo centralizado
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// --- Modelos de Datos para la lista de transacciones ---
data class Transaccion(
    val id: Int,
    val fecha: String,
    val tipo: String,
    val monto: Int,
    val descripcion: String
)

data class PaginatedTransactions(
    val data: List<Transaccion>
)

data class AportesResponse(
    @SerializedName("balance_personal")
    val balancePersonal: Int,
    val transacciones: PaginatedTransactions
)

// --- INICIO DE LA MODIFICACIÓN ---
// Se eliminaron las definiciones duplicadas de 'ChartDataset' y 'ChartData'.
// Ahora, el 'UiState<ChartData>' de abajo usará la versión importada.
// --- FIN DE LA MODIFICACIÓN ---

class AportesViewModel(
    private val token: String,
    private val apiService: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _aportesState = MutableStateFlow<UiState<AportesResponse>>(UiState.Loading)
    val aportesState: StateFlow<UiState<AportesResponse>> = _aportesState

    private val _chartState = MutableStateFlow<UiState<ChartData>>(UiState.Loading)
    val chartState: StateFlow<UiState<ChartData>> = _chartState

    init {
        fetchAportes()
        fetchChartData()
    }

    private fun fetchAportes() {
        viewModelScope.launch {
            _aportesState.value = UiState.Loading
            try {
                val authToken = "Bearer $token"
                val response = apiService.getAportes(authToken)
                _aportesState.value = UiState.Success(response)
            } catch (e: Exception) {
                _aportesState.value = UiState.Error("Error al cargar tus aportes: ${e.message}")
            }
        }
    }

    private fun fetchChartData() {
        viewModelScope.launch {
            _chartState.value = UiState.Loading
            try {
                val authToken = "Bearer $token"
                val response = apiService.getPersonalChartData(authToken)
                _chartState.value = UiState.Success(response)
            } catch (e: Exception) {
                _chartState.value = UiState.Error("Error al cargar datos del gráfico: ${e.message}")
            }
        }
    }
}

class AportesViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AportesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AportesViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}