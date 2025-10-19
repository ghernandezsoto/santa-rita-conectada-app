package cl.santaritaconectada.app.ui.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.response.Evento
import cl.santaritaconectada.app.ui.home.ListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Define el estado específico para esta pantalla
data class EventosState(
    val eventos: ListState<Evento> = ListState.Loading
)

// ViewModel dedicado a los Eventos
class EventoViewModel(private val token: String) : ViewModel() {
    private val _state = MutableStateFlow(EventosState())
    val state: StateFlow<EventosState> = _state.asStateFlow()

    init {
        fetchEventos()
    }

    fun fetchEventos() {
        _state.update { it.copy(eventos = ListState.Loading) }
        val authToken = "Bearer $token"
        RetrofitInstance.api.getEventos(authToken).enqueue(object : Callback<List<Evento>> {
            override fun onResponse(call: Call<List<Evento>>, response: Response<List<Evento>>) {
                if (response.isSuccessful && response.body() != null) {
                    _state.update { it.copy(eventos = ListState.Success(response.body()!!)) }
                } else {
                    _state.update { it.copy(eventos = ListState.Error("Error al cargar eventos")) }
                }
            }

            override fun onFailure(call: Call<List<Evento>>, t: Throwable) {
                _state.update { it.copy(eventos = ListState.Error(t.message ?: "Error de red")) }
            }
        })
    }
}

// Factory para el EventoViewModel
class EventoViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventoViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}