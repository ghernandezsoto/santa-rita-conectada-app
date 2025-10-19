package cl.santaritaconectada.app.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cl.santaritaconectada.app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call // Asumiendo que usas Retrofit
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService"

    /**
     * Se llama cuando un mensaje es recibido.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // --- CORRECCIÓN N° 2 ---
        // Leemos el título y el cuerpo desde AMBAS partes del mensaje.
        // Damos prioridad a la parte 'notification', pero si no existe,
        // usamos la parte 'data' como respaldo.
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"]
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"]

        sendNotification(title, body)
    }

    /**
     * Se llama cuando Firebase genera un nuevo token o el token existente es invalidado.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // --- CORRECCIÓN N° 1 ---
        // Enviamos el token actualizado a nuestro backend inmediatamente.
        sendTokenToServer(token)
    }

    /**
     * Envía el token de registro de FCM a tu backend de Laravel.
     */
    private fun sendTokenToServer(token: String) {
        // AQUÍ DEBES PONER TU LÓGICA DE LLAMADA A LA API
        // Este es un ejemplo conceptual usando Retrofit.
        // Asegúrate de que tu servicio de API y tu modelo de datos coincidan.

        Log.d(TAG, "Intentando enviar token al servidor: $token")

        // Ejemplo: val apiService = ApiClient.retrofit.create(ApiService::class.java)
        // Ejemplo: val call = apiService.registerFcmToken(mapOf("token" to token))

        // call.enqueue(object : Callback<Void> {
        //     override fun onResponse(call: Call<Void>, response: Response<Void>) {
        //         if (response.isSuccessful) {
        //             Log.d(TAG, "Token enviado al servidor exitosamente.")
        //         } else {
        //             Log.e(TAG, "Error al enviar token al servidor. Código: ${response.code()}")
        //         }
        //     }

        //     override fun onFailure(call: Call<Void>, t: Throwable) {
        //         Log.e(TAG, "Fallo en la llamada de red al enviar token.", t)
        //     }
        // })
    }

    /**
     * Crea y muestra una notificación simple.
     */
    private fun sendNotification(title: String?, messageBody: String?) {
        val channelId = "default_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setContentTitle(title ?: "Nuevo Comunicado")
            .setContentText(messageBody ?: "Has recibido una nueva notificación.")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notificaciones Generales", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permiso de notificaciones no concedido. No se puede mostrar la notificación.")
            return
        }

        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        Log.d(TAG, "Notificación mostrada con título: $title")
    }
}