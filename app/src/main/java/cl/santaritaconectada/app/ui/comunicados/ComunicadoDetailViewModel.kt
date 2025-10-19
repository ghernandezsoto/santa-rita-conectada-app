package cl.santaritaconectada.app.ui.comunicados

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.response.Comunicado
import cl.santaritaconectada.app.ui.home.ListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "ComDetailVM"

// Estado para la pantalla de detalle
typealias ComunicadoDetailState = ListState<Comunicado>

class ComunicadoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val token: String
) : ViewModel() {
    private val _state = MutableStateFlow<ComunicadoDetailState>(ListState.Loading)
    val state: StateFlow<ComunicadoDetailState> = _state.asStateFlow()

    private val comunicadoId: Int = checkNotNull(savedStateHandle["comunicadoId"])

    init {
        fetchComunicadoDetail()
    }

    private fun fetchComunicadoDetail() {
        val authToken = "Bearer $token"
        _state.value = ListState.Loading

        RetrofitInstance.api.getComunicadoDetail(authToken, comunicadoId)
            .enqueue(object : Callback<Comunicado> {
                override fun onResponse(call: Call<Comunicado>, response: Response<Comunicado>) {
                    Log.d(TAG, "getComunicadoDetail response code=${response.code()} body=${response.body()}")
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        // En lugar de asumir que todos los subcampos existen, simplemente entregamos el body
                        // La UI debe ser defensiva (usa safe calls). Si quieres, aquí puedes mapear a un modelo UI.
                        _state.value = ListState.Success(listOf(body))
                    } else {
                        val msg = "Error al cargar el detalle (code=${response.code()})"
                        Log.w(TAG, msg)
                        _state.value = ListState.Error(msg)
                    }
                }

                override fun onFailure(call: Call<Comunicado>, t: Throwable) {
                    Log.e(TAG, "getComunicadoDetail failure", t)
                    _state.value = ListState.Error(t.message ?: "Error de red")
                }
            })
    }
}

class ComunicadoDetailViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()
        if (modelClass.isAssignableFrom(ComunicadoDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComunicadoDetailViewModel(savedStateHandle, token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
