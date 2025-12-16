package com.example.guiainteractiva.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.guiainteractiva.ui.theme.DaylightPrimary
import com.example.guiainteractiva.ui.theme.EvergreenDarkPrimary
import com.example.guiainteractiva.ui.theme.EvergreenLightPrimary
import com.example.guiainteractiva.ui.theme.NighttimePrimary
import com.example.guiainteractiva.ui.theme.SunsetPrimary

// Pantalla para que el usuario seleccione el tema de la aplicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val currentTheme by viewModel.themeName.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Elegir Tema") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        ) {
            item { ThemeOptionRow("Evergreen Light", EvergreenLightPrimary, currentTheme == "EvergreenLight") { viewModel.changeTheme("EvergreenLight") } }
            item { ThemeOptionRow("Evergreen Dark", EvergreenDarkPrimary, currentTheme == "EvergreenDark") { viewModel.changeTheme("EvergreenDark") } }
            item { ThemeOptionRow("Daylight", DaylightPrimary, currentTheme == "Daylight") { viewModel.changeTheme("Daylight") } }
            item { ThemeOptionRow("Sunset", SunsetPrimary, currentTheme == "Sunset") { viewModel.changeTheme("Sunset") } }
            item { ThemeOptionRow("Nighttime", NighttimePrimary, currentTheme == "Nighttime") { viewModel.changeTheme("Nighttime") } }
        }
    }
}

// Fila individual para una opción de tema
@Composable
private fun ThemeOptionRow(
    themeName: String,
    themeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(themeColor)
            )
            Text(
                text = themeName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape)
            )
        }
    }
}
