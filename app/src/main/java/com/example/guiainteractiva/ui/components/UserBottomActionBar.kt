package com.example.guiainteractiva.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.guiainteractiva.R

// Barra de acciones inferior para el rol de Usuario normal
@Composable
fun UserBottomActionBar(
    modifier: Modifier = Modifier,
    onPoiListClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón de Ajustes
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = colorScheme.onSurface)
            }

            // Logo del museo en el centro
            Image(
                painter = painterResource(id = R.drawable.logo_museo),
                contentDescription = "Logo del Museo",
                modifier = Modifier.size(40.dp)
            )

            // Botón para la lista de POIs
            IconButton(onClick = onPoiListClick) {
                Icon(Icons.Default.List, contentDescription = "Lista de POIs", tint = colorScheme.onSurface)
            }
        }
    }
}
