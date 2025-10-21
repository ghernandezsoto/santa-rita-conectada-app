package cl.santaritaconectada.app.ui.directivo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.santaritaconectada.app.network.response.User
import cl.santaritaconectada.app.ui.common.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectivoHomeScreen(user: User, token: String, onLogout: () -> Unit) {
    val viewModel: DirectivoDashboardViewModel = viewModel(factory = DirectivoDashboardViewModelFactory(token = token))
    val summaryState by viewModel.summaryState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hola, ${user.name}") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Muestra solo las tarjetas de resumen
            item(span = { GridItemSpan(maxLineSpan) }) {
                SummaryCards(state = summaryState)
            }
        }
    }
}

@Composable
private fun SummaryCards(state: UiState<SummaryResponse>) {
    when (state) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is UiState.Success -> {
            val summary = state.data
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SummaryCard(title = "Total de Socios", value = summary.totalSocios.toString(), modifier = Modifier.weight(1f))
                    SummaryCard(title = "Balance Tesorería", value = "$${summary.balance}", modifier = Modifier.weight(1f), isCurrency = true, valueColor = if (summary.balance >= 0) Color(0xFF16a34a) else MaterialTheme.colorScheme.error)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SummaryCard(title = "Comunicados Recientes", value = summary.comunicadosRecientes.toString(), modifier = Modifier.weight(1f))
                    SummaryCard(title = "Próximos Eventos", value = summary.proximosEventos.toString(), modifier = Modifier.weight(1f))
                }
            }
        }
        is UiState.Error -> {
            Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier, isCurrency: Boolean = false, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = if (isCurrency) 28.sp else 34.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}