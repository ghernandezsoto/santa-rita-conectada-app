package cl.santaritaconectada.app.network.response

import cl.santaritaconectada.app.network.User
import com.google.gson.annotations.SerializedName

data class Evento(
    @SerializedName("id")
    val id: Int,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("fecha_evento")
    val fechaEvento: String,

    @SerializedName("lugar")
    val lugar: String?, // Puede ser nulo

    // Suponemos que la API también nos dará el usuario que creó el evento
    @SerializedName("user")
    val user: User?
)