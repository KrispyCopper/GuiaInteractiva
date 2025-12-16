package com.example.guiainteractiva.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Proveedor de iconos disponibles en la aplicación
object IconProvider {
    val icons = mapOf(
        "location_on" to Icons.Default.LocationOn,
        "star" to Icons.Default.Star,
        "favorite" to Icons.Default.Favorite,
        "home" to Icons.Default.Home,
        "school" to Icons.Default.School,
        "restaurant" to Icons.Default.Restaurant
    )

    fun getIcon(name: String): ImageVector {
        return icons[name] ?: Icons.Default.LocationOn
    }
}

// Proveedor de colores disponibles en la aplicación
object ColorProvider {
    val colors = listOf(
        "#F44336", // Red
        "#4CAF50", // Green
        "#FFC107", // Amber
        "#2196F3", // Blue
        "#9C27B0", // Purple
        "#E91E63"  // Pink
    )
}

// Componente para seleccionar un icono y un color
@Composable
fun IconAndColorPicker(
    selectedIconName: String,
    selectedColorHex: String,
    onIconSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        // Selector de Icono
        Text("Icono", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(IconProvider.icons.keys.toList()) { iconName ->
                val isSelected = selectedIconName == iconName
                val color = try { Color(android.graphics.Color.parseColor(selectedColorHex)) } catch (e: IllegalArgumentException) { MaterialTheme.colorScheme.primary }

                Icon(
                    imageVector = IconProvider.getIcon(iconName),
                    contentDescription = iconName,
                    tint = if (isSelected) color else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onIconSelected(iconName) }
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = if (isSelected) color else Color.Transparent,
                            shape = CircleShape
                        )
                        .padding(8.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Selector de Color
        Text("Color", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(ColorProvider.colors) { colorHex ->
                val isSelected = selectedColorHex == colorHex
                val itemColor = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: IllegalArgumentException) { Color.Black }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(itemColor)
                        .clickable { onColorSelected(colorHex) }
                        .then(
                            if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier
                        )
                )
            }
        }
    }
}
