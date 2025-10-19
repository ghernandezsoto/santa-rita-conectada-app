package cl.santaritaconectada.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.santaritaconectada.app.R

@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    val loginState = loginViewModel.loginState

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- LOGO AÑADIDO AQUÍ ---
            Image(
                painter = painterResource(id = R.drawable.logo), // Asegúrate de que tu logo se llame 'logo.png' o cambia el nombre aquí
                contentDescription = "Logo de Santa Rita Conectada",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            // --- FIN DEL LOGO ---

            Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = loginViewModel.email,
                onValueChange = { loginViewModel.onEmailChange(it) },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (loginState) {
                is LoginState.Loading -> {
                    CircularProgressIndicator()
                }
                else -> {
                    Button(
                        onClick = { loginViewModel.login() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ingresar")
                    }
                }
            }

            if (loginState is LoginState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = loginState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}