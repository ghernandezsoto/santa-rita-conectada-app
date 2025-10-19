package cl.santaritaconectada.app.ui.eventos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.response.Evento
import cl.santaritaconectada.app.ui.home.ListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

typealias EventoDetailState = ListState<Evento>

class EventoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val token: String
) : ViewModel() {
    private val _state = MutableStateFlow<EventoDetailState>(ListState.Loading)
    val state: StateFlow<EventoDetailState> = _state.asStateFlow()

    private val eventoId: Int = checkNotNull(savedStateHandle["eventoId"])

    init {
        fetchEventoDetail()
    }

    private fun fetchEventoDetail() {
        val authToken = "Bearer $token"
        _state.value = ListState.Loading
        RetrofitInstance.api.getEventoDetail(authToken, eventoId).enqueue(object : Callback<Evento> {
            override fun onResponse(call: Call<Evento>, response: Response<Evento>) {
                if (response.isSuccessful && response.body() != null) {
                    _state.value = ListState.Success(listOf(response.body()!!))
                } else {
                    _state.value = ListState.Error("Error al cargar el detalle del evento")
                }
            }
            override fun onFailure(call: Call<Evento>, t: Throwable) {
                _state.value = ListState.Error(t.message ?: "Error de red")
            }
        })
    }
}

class EventoDetailViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        if (modelClass.isAssignableFrom(EventoDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventoDetailViewModel(savedStateHandle, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}