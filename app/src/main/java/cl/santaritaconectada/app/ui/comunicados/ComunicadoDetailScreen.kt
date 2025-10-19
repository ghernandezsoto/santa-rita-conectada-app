package cl.santaritaconectada.app.ui.comunicados

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
fun ComunicadoDetailScreen(viewModel: ComunicadoDetailViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Comunicado") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val detailState = state) {
                is ListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ListState.Success -> {
                    // Usamos firstOrNull() por si la lista viene vacía
                    val comunicado = detailState.items.firstOrNull()
                    if (comunicado == null) {
                        Text(
                            "Comunicado no disponible",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            // Protegemos contra campos nulos con ?: (valores por defecto)
                            Text(
                                text = comunicado.titulo ?: "Sin título",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Publicado por: ${comunicado.user?.name ?: "Autor desconocido"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                            Text(
                                comunicado.contenido ?: "Sin contenido",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                is ListState.Error -> {
                    Text(
                        "Error: ${detailState.message ?: "Error desconocido"}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
