package cl.santaritaconectada.app.ui.home

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
import cl.santaritaconectada.app.network.response.Comunicado
import cl.santaritaconectada.app.network.response.Evento

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    // Observamos el estado del ViewModel. Compose se redibuja automáticamente cuando cambia.
    val state by homeViewModel.homeState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- SECCIÓN DE COMUNICADOS ---
        item {
            Text("Últimos Comunicados", style = MaterialTheme.typography.titleLarge)
        }

        when (val comunicadosState = state.comunicadosState) {
            is ListState.Loading -> {
                item { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            }
            is ListState.Success -> {
                if (comunicadosState.items.isEmpty()) {
                    item { Text("No hay comunicados para mostrar.") }
                } else {
                    items(comunicadosState.items) { comunicado ->
                        ComunicadoCard(comunicado)
                    }
                }
            }
            is ListState.Error -> {
                item { Text("Error: ${comunicadosState.message}", color = MaterialTheme.colorScheme.error) }
            }
        }

        // --- SECCIÓN DE EVENTOS ---
        item {
            Text("Próximos Eventos", style = MaterialTheme.typography.titleLarge)
        }

        when (val eventosState = state.eventosState) {
            is ListState.Loading -> {
                item { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            }
            is ListState.Success -> {
                if (eventosState.items.isEmpty()) {
                    item { Text("No hay eventos próximos.") }
                } else {
                    items(eventosState.items) { evento ->
                        EventoCard(evento)
                    }
                }
            }
            is ListState.Error -> {
                item { Text("Error: ${eventosState.message}", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

// Pequeño Composable para mostrar un comunicado individual
@Composable
fun ComunicadoCard(comunicado: Comunicado) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(comunicado.titulo, fontWeight = FontWeight.Bold)
            Text(comunicado.contenido.take(100) + "...", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text("Publicado por: ${comunicado.user.name}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Pequeño Composable para mostrar un evento individual
@Composable
fun EventoCard(evento: Evento) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(evento.titulo, fontWeight = FontWeight.Bold)
            Text("Fecha: ${evento.fechaEvento}", style = MaterialTheme.typography.bodyMedium)
            Text("Lugar: ${evento.lugar ?: "No especificado"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}