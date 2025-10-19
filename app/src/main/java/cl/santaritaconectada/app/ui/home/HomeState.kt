package cl.santaritaconectada.app.ui.home

import cl.santaritaconectada.app.network.response.Comunicado
import cl.santaritaconectada.app.network.response.Evento

// Un estado genérico para representar la carga de cualquier lista
sealed class ListState<out T> {
    object Loading : ListState<Nothing>()
    data class Success<T>(val items: List<T>) : ListState<T>()
    data class Error(val message: String) : ListState<Nothing>()
}

// El estado completo de la HomeScreen, que contiene el estado de ambas listas
data class HomeState(
    val comunicadosState: ListState<Comunicado> = ListState.Loading,
    val eventosState: ListState<Evento> = ListState.Loading
)