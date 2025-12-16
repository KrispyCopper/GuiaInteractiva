package com.example.guiainteractiva.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Barra de acciones inferior para el rol de Administrador
@Composable
fun AdminBottomActionBar(
    modifier: Modifier = Modifier,
    onPoiListClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.92f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón de Ajustes
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Ajustes", tint = colorScheme.onSurface)
            }
            // Botón para la lista de POIs
            IconButton(onClick = onPoiListClick) {
                Icon(Icons.Default.List, contentDescription = "Lista de POIs", tint = colorScheme.onSurface)
            }

            // Botón flotante para añadir POI
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Add, "Añadir Punto")
            }

            // Botón de Editar/Mover
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar/Mover POI", tint = colorScheme.onSurface)
            }
            // Botón de Eliminar
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar POI", tint = colorScheme.onSurface)
            }
        }
    }
}
