package cl.santaritaconectada.app.ui.common

/**
 * Una clase sellada (sealed class) para representar los 3 estados posibles
 * de una petición de datos a la API.
 * Es genérica (<T>) para poder reutilizarla con cualquier tipo de dato.
 */
sealed class UiState<out T> {
    // 1. Estado de carga (Loading)
    object Loading : UiState<Nothing>()

    // 2. Estado de éxito (Success)
    data class Success<out T>(val data: T) : UiState<T>()

    // 3. Estado de error (Error)
    data class Error(val message: String) : UiState<Nothing>()
}