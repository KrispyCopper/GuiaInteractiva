package com.example.guiainteractiva.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.guiainteractiva.ui.login.LoginScreen
import com.example.guiainteractiva.ui.login.RegisterScreen
import com.example.guiainteractiva.ui.map.MapScreen

// Objeto que contiene las rutas de navegación como constantes para evitar errores de tipeo.
object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAP = "map"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.MAP)
                },
                onNavigateToRegister = { navController.navigate(AppRoutes.REGISTER) }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(AppRoutes.LOGIN) },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.MAP) {
            MapScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
