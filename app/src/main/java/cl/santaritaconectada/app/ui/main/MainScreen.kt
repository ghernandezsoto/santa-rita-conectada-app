package cl.santaritaconectada.app.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import cl.santaritaconectada.app.network.User
import cl.santaritaconectada.app.ui.comunicados.*
import cl.santaritaconectada.app.ui.eventos.*
import cl.santaritaconectada.app.ui.home.HomeScreen
import cl.santaritaconectada.app.ui.home.HomeViewModel
import cl.santaritaconectada.app.ui.home.HomeViewModelFactory
import cl.santaritaconectada.app.ui.profile.ProfileScreen

sealed class Screen(val route: String) {
    sealed class MainScreen(route: String, val label: String, val icon: ImageVector) : Screen(route) {
        object Home : MainScreen("home", "Inicio", Icons.Default.Home)
        object Comunicados : MainScreen("comunicados", "Comunicados", Icons.Default.MailOutline)
        object Eventos : MainScreen("eventos", "Eventos", Icons.Default.DateRange)
        object Profile : MainScreen("profile", "Mi Perfil", Icons.Default.Person)
    }
    object ComunicadoDetail : Screen("comunicados/{comunicadoId}")
    object EventoDetail : Screen("eventos/{eventoId}")
}

val items = listOf(
    Screen.MainScreen.Home,
    Screen.MainScreen.Comunicados,
    Screen.MainScreen.Eventos,
    Screen.MainScreen.Profile
)

@Composable
fun MainScreen(user: User, token: String, onLogout: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        // ✅ Usamos tipografía pequeña para que no se corte
                        label = {
                            Text(
                                text = screen.label,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
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
        NavHost(navController, startDestination = Screen.MainScreen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.MainScreen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(token))
                HomeScreen(homeViewModel)
            }
            composable(Screen.MainScreen.Comunicados.route) {
                val comunicadoViewModel: ComunicadoViewModel = viewModel(factory = ComunicadoViewModelFactory(token))
                ComunicadosScreen(viewModel = comunicadoViewModel, navController = navController)
            }
            composable(Screen.MainScreen.Eventos.route) {
                val eventoViewModel: EventoViewModel = viewModel(factory = EventoViewModelFactory(token))
                EventosScreen(viewModel = eventoViewModel, navController = navController)
            }
            composable(Screen.MainScreen.Profile.route) {
                ProfileScreen(userName = user.name, onLogoutClicked = onLogout)
            }
            composable(
                route = Screen.ComunicadoDetail.route,
                arguments = listOf(navArgument("comunicadoId") { type = NavType.IntType })
            ) {
                val detailViewModel: ComunicadoDetailViewModel = viewModel(factory = ComunicadoDetailViewModelFactory(token))
                ComunicadoDetailScreen(viewModel = detailViewModel, navController = navController)
            }
            composable(
                route = Screen.EventoDetail.route,
                arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
            ) {
                val detailViewModel: EventoDetailViewModel = viewModel(factory = EventoDetailViewModelFactory(token))
                EventoDetailScreen(viewModel = detailViewModel, navController = navController)
            }
        }
    }
}
