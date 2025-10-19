package cl.santaritaconectada.app.network.response

import cl.santaritaconectada.app.network.User
import com.google.gson.annotations.SerializedName

data class Comunicado(
    @SerializedName("id")
    val id: Int,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("contenido")
    val contenido: String,

    @SerializedName("fecha_envio")
    val fechaEnvio: String?, // Puede ser nulo si es un borrador

    @SerializedName("created_at")
    val createdAt: String,

    // Objeto anidado para el usuario que creó el comunicado
    @SerializedName("user")
    val user: User
)