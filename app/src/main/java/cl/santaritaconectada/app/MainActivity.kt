package cl.santaritaconectada.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import cl.santaritaconectada.app.ui.login.LoginScreen
import cl.santaritaconectada.app.ui.login.LoginState
import cl.santaritaconectada.app.ui.login.LoginViewModel
import cl.santaritaconectada.app.ui.main.MainScreen
import cl.santaritaconectada.app.ui.theme.SantaRitaConectadaTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    // --- INICIO DEL CÓDIGO AÑADIDO ---

    // 1. Creamos el lanzador para la solicitud de permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso de notificaciones CONCEDIDO.")
            getAndLogFcmToken() // Si nos dan permiso, obtenemos el token
        } else {
            Log.w("MainActivity", "Permiso de notificaciones DENEGADO.")
        }
    }

    // 2. Creamos la función para pedir el permiso
    private fun askNotificationPermission() {
        // Esta función solo se aplica para Android 13 (API 33) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Si ya tenemos el permiso, obtenemos el token
                getAndLogFcmToken()
            } else {
                // Si no, lanzamos la petición de permiso
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Para versiones de Android más antiguas, el permiso se concede al instalar
            getAndLogFcmToken()
        }
    }

    // 3. Creamos la función para obtener y mostrar el token de Firebase
    private fun getAndLogFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            // Este Log es el que buscaremos para verificar
            Log.d("FCM_TOKEN", "El token del dispositivo es: $token")
        }
    }

    // --- FIN DEL CÓDIGO AÑADIDO ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission() // <-- Llamamos a la función para pedir permiso al iniciar

        enableEdgeToEdge()
        setContent {
            SantaRitaConectadaTheme {
                val state = loginViewModel.loginState

                when (state) {
                    is LoginState.Success -> {
                        MainScreen(
                            user = state.user,
                            token = state.token,
                            onLogout = { loginViewModel.logout() }
                        )
                    }
                    else -> {
                        LoginScreen(loginViewModel = loginViewModel)
                    }
                }
            }
        }
    }
}