package cl.santaritaconectada.app.network.request

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
    @SerializedName("token")
    val token: String
)