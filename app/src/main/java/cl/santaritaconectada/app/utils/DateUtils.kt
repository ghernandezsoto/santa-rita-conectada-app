package cl.santaritaconectada.app.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formatea una cadena de fecha en formato ISO a "dd/MM/yyyy".
 * Si el formato falla, devuelve la cadena original.
 */
fun formatDate(dateString: String?): String {
    if (dateString == null) return ""
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        parser.parse(dateString)?.let { formatter.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString // Devuelve la fecha original si hay un error de formato
    }
}