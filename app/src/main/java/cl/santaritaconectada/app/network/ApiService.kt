package cl.santaritaconectada.app.network

import cl.santaritaconectada.app.network.request.FcmTokenRequest
import cl.santaritaconectada.app.network.request.LoginRequest
import cl.santaritaconectada.app.network.response.Acta
import cl.santaritaconectada.app.network.response.ChartData
import cl.santaritaconectada.app.network.response.Comunicado
import cl.santaritaconectada.app.network.response.Documento
import cl.santaritaconectada.app.network.response.Evento
import cl.santaritaconectada.app.network.response.LoginResponse
import cl.santaritaconectada.app.network.response.User
import cl.santaritaconectada.app.ui.directivo.SummaryResponse
import cl.santaritaconectada.app.ui.socio.AportesResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("api/user")
    fun getAuthenticatedUser(@Header("Authorization") token: String): Call<User>

    @POST("api/fcm-token")
    fun registerFcmToken(
        @Header("Authorization") token: String,
        @Body fcmTokenRequest: FcmTokenRequest
    ): Call<Unit>

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

    @GET("api/documentos")
    suspend fun getDocumentos(@Header("Authorization") token: String): List<Documento>

    @GET("api/actas")
    suspend fun getActas(@Header("Authorization") token: String): List<Acta>

    @GET("api/aportes")
    suspend fun getAportes(@Header("Authorization") token: String): AportesResponse

    @GET("api/charts/personal-finances")
    suspend fun getPersonalChartData(@Header("Authorization") token: String): ChartData

    @GET("api/directivo/summary")
    suspend fun getDirectivoSummary(@Header("Authorization") token: String): SummaryResponse

    @GET("api/charts/finances")
    suspend fun getFinanceChart(@Header("Authorization") token: String): ChartData
}