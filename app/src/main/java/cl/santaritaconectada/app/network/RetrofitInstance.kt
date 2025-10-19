package cl.santaritaconectada.app.network

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // Apuntamos a la dirección IP pública servidor en DigitalOcean.
    private const val BASE_URL = "http://165.232.144.157/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor para loguear request (headers + body)
    private val rawRequestInterceptor = Interceptor { chain ->
        val request: Request = chain.request()

        // Log request line + headers
        Log.e("RAW_REQUEST", "URL: ${request.url}  --- Method: ${request.method}")
        Log.e("RAW_REQUEST", "Headers: ${request.headers}")

        // Log request body if existe
        val reqBody = try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            null
        }
        Log.e("RAW_REQUEST", "Body: $reqBody")

        chain.proceed(request)
    }

    // Interceptor que captura response y la loguea, luego recompone el body
    private val rawResponseInterceptor = Interceptor { chain ->
        val response: Response = chain.proceed(chain.request())
        val responseBody = response.body
        val content = try { responseBody?.string() } catch (e: Exception) { null }

        Log.e("RAW_RESPONSE", "URL: ${response.request.url}  --- HTTP ${response.code}\nBody: $content")

        // Reconstruir body para que Retrofit lo siga consumiendo
        val mediaType = responseBody?.contentType()?.toString()?.toMediaTypeOrNull()
        val newBody = ResponseBody.create(mediaType, content ?: "")
        response.newBuilder().body(newBody).build()
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(rawRequestInterceptor)
        .addInterceptor(rawResponseInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val gson = GsonBuilder()
        .setLenient() // temporal para debug
        .create()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}