package cl.santaritaconectada.app.ui.socio

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.santaritaconectada.app.network.response.User
import cl.santaritaconectada.app.ui.comunicados.ComunicadoViewModel
import cl.santaritaconectada.app.ui.comunicados.ComunicadoViewModelFactory
import cl.santaritaconectada.app.ui.comunicados.ComunicadosScreen
import cl.santaritaconectada.app.ui.eventos.EventoViewModel
import cl.santaritaconectada.app.ui.eventos.EventoViewModelFactory
import cl.santaritaconectada.app.ui.eventos.EventosScreen
import cl.santaritaconectada.app.ui.profile.ProfileScreen

data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun SocioHomeScreen(user: User, token: String, onLogout: () -> Unit) {
    val navController = rememberNavController()

    val items = listOf(
        NavItem("home", "Principal", Icons.Default.Home),
        NavItem("aportes", "Aportes", Icons.Default.Savings),
        NavItem("comunicados", "Noticias", Icons.Default.Campaign),
        NavItem("eventos", "Eventos", Icons.Default.Event),
        NavItem("perfil", "Perfil", Icons.Default.Person),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home", Modifier.padding(innerPadding)) {
            // --- INICIO DE LA MODIFICACIÓN ---
            // Ahora le pasamos el token a la pantalla del dashboard.
            composable("home") { SocioDashboardScreen(user = user, token = token) }
            // --- FIN DE LA MODIFICACIÓN ---

            composable("aportes") { AportesScreen(token = token) }

            composable("comunicados") {
                val vm: ComunicadoViewModel = viewModel(factory = ComunicadoViewModelFactory(token = token))
                ComunicadosScreen(viewModel = vm, navController = navController)
            }
            composable("eventos") {
                val vm: EventoViewModel = viewModel(factory = EventoViewModelFactory(token = token))
                EventosScreen(viewModel = vm, navController = navController)
            }
            composable("perfil") { ProfileScreen(userName = user.name, onLogoutClicked = onLogout) }
        }
    }
}