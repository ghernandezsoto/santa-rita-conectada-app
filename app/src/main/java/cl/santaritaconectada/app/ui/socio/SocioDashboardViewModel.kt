package cl.santaritaconectada.app.ui.socio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.santaritaconectada.app.network.ApiService
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.response.Acta
import cl.santaritaconectada.app.network.response.Documento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Loading : UiState<Nothing>()
}

class SocioDashboardViewModel(
    private val token: String, // <-- 1. AHORA REQUIERE EL TOKEN
    private val apiService: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _documentosState = MutableStateFlow<UiState<List<Documento>>>(UiState.Loading)
    val documentosState: StateFlow<UiState<List<Documento>>> = _documentosState

    private val _actasState = MutableStateFlow<UiState<List<Acta>>>(UiState.Loading)
    val actasState: StateFlow<UiState<List<Acta>>> = _actasState

    init {
        fetchDocumentos()
        fetchActas()
    }

    private fun fetchDocumentos() {
        viewModelScope.launch {
            try {
                val authToken = "Bearer $token" // Se prepara el token para la autorización
                val response = apiService.getDocumentos(authToken).take(5) // <-- 2. SE USA EL TOKEN
                _documentosState.value = UiState.Success(response)
            } catch (e: Exception) {
                _documentosState.value = UiState.Error("Error al cargar documentos: ${e.message}")
            }
        }
    }

    private fun fetchActas() {
        viewModelScope.launch {
            try {
                val authToken = "Bearer $token" // Se prepara el token para la autorización
                val response = apiService.getActas(authToken).take(5) // <-- 2. SE USA EL TOKEN
                _actasState.value = UiState.Success(response)
            } catch (e: Exception) {
                _actasState.value = UiState.Error("Error al cargar actas: ${e.message}")
            }
        }
    }
}

// 3. FACTORY PARA PODER CREAR EL VIEWMODEL CON EL TOKEN
class SocioDashboardViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SocioDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SocioDashboardViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}