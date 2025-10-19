package cl.santaritaconectada.app.network

import cl.santaritaconectada.app.network.request.FcmTokenRequest
import cl.santaritaconectada.app.network.request.LoginRequest
import cl.santaritaconectada.app.network.response.Comunicado
import cl.santaritaconectada.app.network.response.Evento
import cl.santaritaconectada.app.network.response.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/user")
    fun getAuthenticatedUser(@Header("Authorization") token: String): Call<User>

    // --- ENDPOINT NUEVO PARA REGISTRAR EL TOKEN DE NOTIFICACIONES ---
    @POST("api/fcm-token")
    fun registerFcmToken(
        @Header("Authorization") token: String,
        @Body fcmTokenRequest: FcmTokenRequest
    ): Call<Unit> // Usamos Call<Unit> porque no esperamos una respuesta con datos

    @GET("api/comunicados")
    fun getComunicados(@Header("Authorization") token: String): Call<List<Comunicado>>

    @GET("api/comunicados/{id}")
    fun getComunicadoDetail(
        @Header("Authorization") token: String,
        @Path("id") comunicadoId: Int
    ): Call<Comunicado>

    @GET("api/eventos")
    fun getEventos(@Header("Authorization") token: String): Call<List<Evento>>

    @GET("api/eventos/{id}")
    fun getEventoDetail(
        @Header("Authorization") token: String,
        @Path("id") eventoId: Int
    ): Call<Evento>
}