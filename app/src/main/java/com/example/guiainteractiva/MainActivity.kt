package com.example.guiainteractiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.guiainteractiva.data.UserPreferencesRepository
import com.example.guiainteractiva.navigation.AppNavigation
import com.example.guiainteractiva.ui.settings.SettingsViewModel
import com.example.guiainteractiva.ui.settings.SettingsViewModelFactory
import com.example.guiainteractiva.ui.theme.GuiaInteractivaTheme

class MainActivity : ComponentActivity() {

    // Repositorio para las preferencias del usuario
    private val userPreferencesRepository by lazy { UserPreferencesRepository(this) }

    // ViewModel para los ajustes
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(userPreferencesRepository)
    }

    // Punto de entrada de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Observa el nombre del tema desde el ViewModel
            val themeName by settingsViewModel.themeName.collectAsState()

            // Aplica el tema dinámicamente
            GuiaInteractivaTheme(themeName = themeName) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Inicia la navegación de la app
                    AppNavigation(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
