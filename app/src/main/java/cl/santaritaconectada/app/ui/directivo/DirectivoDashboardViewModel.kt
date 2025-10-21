package cl.santaritaconectada.app.ui.directivo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.santaritaconectada.app.network.ApiService
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.response.ChartData
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import cl.santaritaconectada.app.ui.common.UiState

// Modelo de datos para las tarjetas de resumen
data class SummaryResponse(
    @SerializedName("total_socios")
    val totalSocios: Int,
    val balance: Long,
    @SerializedName("comunicados_recientes")
    val comunicadosRecientes: Int,
    @SerializedName("proximos_eventos")
    val proximosEventos: Int
)

// ViewModel (El "Cerebro")
class DirectivoDashboardViewModel(
    private val token: String,
    private val apiService: ApiService = RetrofitInstance.api
) : ViewModel() {

    // Estado para las tarjetas de resumen
    private val _summaryState = MutableStateFlow<UiState<SummaryResponse>>(UiState.Loading)
    val summaryState: StateFlow<UiState<SummaryResponse>> = _summaryState

    // Estado para el gráfico de finanzas
    private val _chartState = MutableStateFlow<UiState<ChartData>>(UiState.Loading)
    val chartState: StateFlow<UiState<ChartData>> = _chartState

    init {
        fetchSummary()
        fetchChartData()
    }

    private fun fetchSummary() {
        viewModelScope.launch {
            _summaryState.value = UiState.Loading
            try {
                val authToken = "Bearer $token"
                val response = apiService.getDirectivoSummary(authToken)
                _summaryState.value = UiState.Success(response)
            } catch (e: Exception) {
                _summaryState.value = UiState.Error("Error al cargar el resumen: ${e.message}")
            }
        }
    }

    private fun fetchChartData() {
        viewModelScope.launch {
            _chartState.value = UiState.Loading
            try {
                val authToken = "Bearer $token"
                val response = apiService.getFinanceChart(authToken)
                _chartState.value = UiState.Success(response)
            } catch (e: Exception) {
                _chartState.value = UiState.Error("Error al cargar datos del gráfico: ${e.message}")
            }
        }
    }
}

// Factory para crear el ViewModel con el token
class DirectivoDashboardViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DirectivoDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DirectivoDashboardViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}