package cl.santaritaconectada.app.ui.comunicados

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.santaritaconectada.app.network.response.Comunicado
import cl.santaritaconectada.app.ui.home.ListState

@Composable
fun ComunicadosScreen(viewModel: ComunicadoViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (val comunicadosState = state.comunicados) {
            is ListState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ListState.Success -> {
                if (comunicadosState.items.isEmpty()) {
                    Text("No hay comunicados para mostrar.", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(comunicadosState.items) { comunicado ->
                            ComunicadoCard(
                                comunicado = comunicado,
                                onClick = {
                                    // Navega a la pantalla de detalle con el ID del comunicado
                                    navController.navigate("comunicados/${comunicado.id}")
                                }
                            )
                        }
                    }
                }
            }
            is ListState.Error -> {
                Text(
                    "Error: ${comunicadosState.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ComunicadoCard(comunicado: Comunicado, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // <-- La tarjeta ahora es clickeable
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(comunicado.titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(comunicado.contenido.take(150) + "...", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "Publicado por: ${comunicado.user.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}