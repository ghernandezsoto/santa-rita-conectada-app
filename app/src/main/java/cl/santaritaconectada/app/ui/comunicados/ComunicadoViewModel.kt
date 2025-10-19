package cl.santaritaconectada.app.ui.comunicados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.response.Comunicado
import cl.santaritaconectada.app.ui.home.ListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Define el estado específico para esta pantalla
data class ComunicadosState(
    val comunicados: ListState<Comunicado> = ListState.Loading
)

// ViewModel dedicado a los Comunicados
class ComunicadoViewModel(private val token: String) : ViewModel() {
    private val _state = MutableStateFlow(ComunicadosState())
    val state: StateFlow<ComunicadosState> = _state.asStateFlow()

    init {
        fetchComunicados()
    }

    fun fetchComunicados() {
        _state.update { it.copy(comunicados = ListState.Loading) }
        val authToken = "Bearer $token"
        RetrofitInstance.api.getComunicados(authToken).enqueue(object : Callback<List<Comunicado>> {
            override fun onResponse(call: Call<List<Comunicado>>, response: Response<List<Comunicado>>) {
                if (response.isSuccessful && response.body() != null) {
                    _state.update { it.copy(comunicados = ListState.Success(response.body()!!)) }
                } else {
                    _state.update { it.copy(comunicados = ListState.Error("Error al cargar comunicados")) }
                }
            }

            override fun onFailure(call: Call<List<Comunicado>>, t: Throwable) {
                _state.update { it.copy(comunicados = ListState.Error(t.message ?: "Error de red")) }
            }
        })
    }
}

// Factory para el ComunicadoViewModel
class ComunicadoViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComunicadoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComunicadoViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}