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
import cl.santaritaconectada.app.ui.directivo.DirectivoHomeScreen
import cl.santaritaconectada.app.ui.login.LoginScreen
import cl.santaritaconectada.app.ui.login.LoginState
import cl.santaritaconectada.app.ui.login.LoginViewModel
import cl.santaritaconectada.app.ui.socio.SocioHomeScreen
import cl.santaritaconectada.app.ui.theme.SantaRitaConectadaTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso de notificaciones CONCEDIDO.")
            getAndLogFcmToken()
        } else {
            Log.w("MainActivity", "Permiso de notificaciones DENEGADO.")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                getAndLogFcmToken()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            getAndLogFcmToken()
        }
    }

    private fun getAndLogFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_TOKEN", "El token del dispositivo es: $token")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()

        enableEdgeToEdge()
        setContent {
            SantaRitaConectadaTheme {
                val state = loginViewModel.loginState

                when (state) {
                    is LoginState.Success -> {
                        val isSocio = state.user.roles.any { it.name == "Socio" }

                        if (isSocio) {
                            // --- SOCIO ---
                            SocioHomeScreen(
                                user = state.user,
                                token = state.token, // Se pasa el token que faltaba
                                onLogout = { loginViewModel.logout() }
                            )
                        } else {
                            // --- DIRECTIVO ---
                            DirectivoHomeScreen(
                                user = state.user,
                                token = state.token, // ✅ Corregido: se añadió token
                                onLogout = { loginViewModel.logout() }
                            )
                        }
                    }
                    else -> {
                        LoginScreen(loginViewModel = loginViewModel)
                    }
                }

            }
        }
    }
}
