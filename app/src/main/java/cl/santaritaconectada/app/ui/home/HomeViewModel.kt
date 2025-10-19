package cl.santaritaconectada.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.response.Comunicado
import cl.santaritaconectada.app.network.response.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val token: String) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        // Al crearse el ViewModel, busca inmediatamente los datos
        fetchComunicados()
        fetchEventos()
    }

    private fun fetchComunicados() {
        val authToken = "Bearer $token"
        RetrofitInstance.api.getComunicados(authToken).enqueue(object : Callback<List<Comunicado>> {
            override fun onResponse(call: Call<List<Comunicado>>, response: Response<List<Comunicado>>) {
                if (response.isSuccessful && response.body() != null) {
                    _homeState.update { it.copy(comunicadosState = ListState.Success(response.body()!!)) }
                } else {
                    _homeState.update { it.copy(comunicadosState = ListState.Error("Error al cargar comunicados")) }
                }
            }

            override fun onFailure(call: Call<List<Comunicado>>, t: Throwable) {
                _homeState.update { it.copy(comunicadosState = ListState.Error(t.message ?: "Error de red")) }
            }
        })
    }

    private fun fetchEventos() {
        val authToken = "Bearer $token"
        RetrofitInstance.api.getEventos(authToken).enqueue(object : Callback<List<Evento>> {
            override fun onResponse(call: Call<List<Evento>>, response: Response<List<Evento>>) {
                if (response.isSuccessful && response.body() != null) {
                    _homeState.update { it.copy(eventosState = ListState.Success(response.body()!!)) }
                } else {
                    _homeState.update { it.copy(eventosState = ListState.Error("Error al cargar eventos")) }
                }
            }

            override fun onFailure(call: Call<List<Evento>>, t: Throwable) {
                _homeState.update { it.copy(eventosState = ListState.Error(t.message ?: "Error de red")) }
            }
        })
    }
}

// Factory para poder pasar el token al ViewModel
class HomeViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}