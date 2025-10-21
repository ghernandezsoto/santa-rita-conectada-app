package cl.santaritaconectada.app.ui.socio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.santaritaconectada.app.utils.formatDate

@Composable
fun AportesScreen(
    token: String,
    viewModel: AportesViewModel = viewModel(factory = AportesViewModelFactory(token = token))
) {
    val aportesState by viewModel.aportesState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta de Saldo
        item {
            when (val state = aportesState) {
                is UiState.Loading -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
                    }
                }
                is UiState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tu Saldo Actual", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$${state.data.balancePersonal}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (state.data.balancePersonal >= 0) Color(0xFF16a34a) else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Historial de Transacciones
        item {
            when (val state = aportesState) {
                is UiState.Success -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Historial de Movimientos", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            if (state.data.transacciones.data.isEmpty()) {
                                Text("No tienes transacciones registradas.")
                            } else {
                                state.data.transacciones.data.forEach { transaccion ->
                                    TransaccionListItem(transaccion)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun TransaccionListItem(transaccion: Transaccion) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(transaccion.descripcion, fontWeight = FontWeight.Medium)
            Text(formatDate(transaccion.fecha), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = "${if (transaccion.tipo == "Ingreso") "+" else "-"} $${transaccion.monto}",
            fontWeight = FontWeight.Medium,
            color = if (transaccion.tipo == "Ingreso") Color(0xFF16a34a) else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}