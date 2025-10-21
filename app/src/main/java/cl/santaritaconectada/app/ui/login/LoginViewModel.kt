package cl.santaritaconectada.app.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.santaritaconectada.app.network.RetrofitInstance
import cl.santaritaconectada.app.network.request.FcmTokenRequest
import cl.santaritaconectada.app.network.request.LoginRequest
import cl.santaritaconectada.app.network.response.LoginResponse
import cl.santaritaconectada.app.network.response.User // <-- IMPORTACIÓN CORREGIDA
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User, val token: String) : LoginState() // <-- AHORA USA EL USER CORRECTO
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loginState by mutableStateOf<LoginState>(LoginState.Idle)
        private set

    fun onEmailChange(newEmail: String) { email = newEmail }
    fun onPasswordChange(newPassword: String) { password = newPassword }

    fun login() {
        loginState = LoginState.Loading
        val request = LoginRequest(email.trim(), password.trim(), "Android App")

        RetrofitInstance.api.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    fetchUserData(token)
                } else {
                    loginState = LoginState.Error("Credenciales incorrectas.")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginState = LoginState.Error("Error de conexión: ${t.message}")
            }
        })
    }

    private fun fetchUserData(token: String) {
        val authToken = "Bearer $token"
        // AHORA USA EL USER CORRECTO
        RetrofitInstance.api.getAuthenticatedUser(authToken).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null) {
                    registerDeviceToken(authToken)
                    loginState = LoginState.Success(response.body()!!, token)
                } else {
                    loginState = LoginState.Error("No se pudieron obtener los datos del usuario.")
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                loginState = LoginState.Error("Error de conexión al obtener datos del usuario.")
            }
        })
    }

    private fun registerDeviceToken(authToken: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN_REG", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val fcmToken = task.result
            Log.d("FCM_TOKEN_REG", "Token de FCM obtenido: $fcmToken")

            val request = FcmTokenRequest(token = fcmToken)
            RetrofitInstance.api.registerFcmToken(authToken, request).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if(response.isSuccessful) {
                        Log.d("FCM_TOKEN_REG", "Token de FCM registrado exitosamente en el servidor.")
                    } else {
                        Log.e("FCM_TOKEN_REG", "Error al registrar el token en el servidor: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("FCM_TOKEN_REG", "Falla de red al registrar el token de FCM", t)
                }
            })
        }
    }

    fun logout() {
        loginState = LoginState.Idle
    }
}