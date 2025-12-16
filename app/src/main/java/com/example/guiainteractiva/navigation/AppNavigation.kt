package com.example.guiainteractiva.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.guiainteractiva.ui.login.LoginScreen
import com.example.guiainteractiva.ui.login.RegisterScreen
import com.example.guiainteractiva.ui.map.MapScreen
import com.example.guiainteractiva.ui.map.MapViewModel
import com.example.guiainteractiva.ui.settings.SettingsScreen
import com.example.guiainteractiva.ui.settings.SettingsViewModel

// Define las rutas de navegación de la aplicación
object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAP = "map"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()

    // Host de navegación que gestiona las pantallas
    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        // Ruta para la pantalla de Login
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.MAP) { popUpTo(AppRoutes.LOGIN) { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(AppRoutes.REGISTER) }
            )
        }

        // Ruta para la pantalla de Registro
        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Ruta para la pantalla del Mapa
        composable(AppRoutes.MAP) {
            val mapViewModel: MapViewModel = viewModel()
            MapScreen(
                mapViewModel = mapViewModel,
                onNavigateToSettings = { navController.navigate(AppRoutes.SETTINGS) },
                onSignOut = {
                    navController.navigate(AppRoutes.LOGIN) { popUpTo(AppRoutes.MAP) { inclusive = true } }
                }
            )
        }

        // Ruta para la pantalla de Ajustes
        composable(AppRoutes.SETTINGS) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
