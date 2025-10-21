package cl.santaritaconectada.app.ui.socio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.santaritaconectada.app.network.response.Acta
import cl.santaritaconectada.app.network.response.Documento
import cl.santaritaconectada.app.network.response.User
import cl.santaritaconectada.app.utils.formatDate // <-- SE AÑADE LA IMPORTACIÓN DE LA FUNCIÓN PÚBLICA

@Composable
fun SocioDashboardScreen(
    user: User,
    token: String,
    viewModel: SocioDashboardViewModel = viewModel(factory = SocioDashboardViewModelFactory(token = token))
) {
    val documentosState by viewModel.documentosState.collectAsState()
    val actasState by viewModel.actasState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta de Bienvenida
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "¡Bienvenido, ${user.name}! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Este es tu portal personal. Aquí encontrarás las últimas noticias y acceso a documentos importantes de nuestra comunidad.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sección de Últimos Documentos
        item {
            FileListCard(
                title = "Últimos Documentos",
                state = documentosState,
                itemName = { (it as Documento).nombre_documento },
                itemDate = { (it as Documento).created_at }
            )
        }

        // Sección de Últimas Actas
        item {
            FileListCard(
                title = "Últimas Actas",
                state = actasState,
                itemName = { (it as Acta).titulo },
                itemDate = { (it as Acta).created_at }
            )
        }
    }
}

@Composable
private fun <T> FileListCard(
    title: String,
    state: UiState<List<T>>,
    itemName: (T) -> String,
    itemDate: (T) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            when (state) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text("No hay elementos disponibles.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        state.data.forEach { item ->
                            FileListItem(name = itemName(item), date = itemDate(item))
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
                is UiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun FileListItem(name: String, date: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = formatDate(date), // <-- Ahora usa la función pública
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}