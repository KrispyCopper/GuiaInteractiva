package com.example.guiainteractiva.ui.map

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.guiainteractiva.model.Poi

// Pin que se muestra sobre el mapa y representa un POI
@Composable
fun PoiPin(
    poi: Poi,
    onPoiClick: (Poi) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onPoiClick(poi) },
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Punto de Interés",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(40.dp)
        )
    }
}
