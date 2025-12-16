package com.example.guiainteractiva.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Instancia única del DataStore para toda la aplicación
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// Repositorio para gestionar las preferencias del usuario
class UserPreferencesRepository(context: Context) {

    private val dataStore = context.dataStore

    // Claves para acceder a las preferencias guardadas
    private object PreferencesKeys {
        val THEME_NAME = stringPreferencesKey("theme_name")
    }

    // Flujo que emite el nombre del tema guardado
    val themeName: Flow<String> = dataStore.data.map {
        preferences -> preferences[PreferencesKeys.THEME_NAME] ?: "EvergreenLight"
    }

    // Guarda la preferencia del tema seleccionada por el usuario
    suspend fun saveThemePreference(themeName: String) {
        dataStore.edit {
            preferences -> preferences[PreferencesKeys.THEME_NAME] = themeName
        }
    }
}
