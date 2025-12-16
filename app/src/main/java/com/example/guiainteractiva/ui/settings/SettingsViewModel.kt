package com.example.guiainteractiva.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.guiainteractiva.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    // Expone el nombre del tema como un StateFlow para que la UI lo observe
    val themeName: StateFlow<String> = userPreferencesRepository.themeName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "EvergreenLight" // Valor por defecto
        )

    // Función para que la UI solicite el cambio de tema
    fun changeTheme(theme: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreference(theme)
        }
    }
}

// Factoría para crear una instancia de SettingsViewModel con sus dependencias
class SettingsViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
