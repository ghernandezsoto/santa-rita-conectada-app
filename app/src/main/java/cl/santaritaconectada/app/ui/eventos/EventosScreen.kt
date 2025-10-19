package cl.santaritaconectada.app.ui.eventos

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
import cl.santaritaconectada.app.network.response.Evento
import cl.santaritaconectada.app.ui.home.ListState

@Composable
fun EventosScreen(viewModel: EventoViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (val eventosState = state.eventos) {
            is ListState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ListState.Success -> {
                if (eventosState.items.isEmpty()) {
                    Text("No hay eventos próximos.", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(eventosState.items) { evento ->
                            EventoCard(
                                evento = evento,
                                onClick = {
                                    navController.navigate("eventos/${evento.id}")
                                }
                            )
                        }
                    }
                }
            }
            is ListState.Error -> {
                Text("Error: ${eventosState.message}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun EventoCard(evento: Evento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(evento.titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Fecha: ${evento.fechaEvento}", style = MaterialTheme.typography.bodyMedium)
            Text("Lugar: ${evento.lugar ?: "No especificado"}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}