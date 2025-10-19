package cl.santaritaconectada.app.ui.eventos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.santaritaconectada.app.ui.home.ListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventoDetailScreen(viewModel: EventoDetailViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            when (val detailState = state) {
                is ListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ListState.Success -> {
                    val evento = detailState.items.first()
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(evento.titulo, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                        Text("Fecha: ${evento.fechaEvento}", style = MaterialTheme.typography.titleMedium)
                        Text("Lugar: ${evento.lugar ?: "No especificado"}", style = MaterialTheme.typography.titleMedium)
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        Text(evento.descripcion, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                is ListState.Error -> {
                    Text("Error: ${detailState.message}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}