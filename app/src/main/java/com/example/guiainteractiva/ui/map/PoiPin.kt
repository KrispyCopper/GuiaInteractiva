package com.example.guiainteractiva.ui.map

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.guiainteractiva.model.MapMode
import com.example.guiainteractiva.model.Poi
import com.example.guiainteractiva.ui.components.IconProvider

// Representa un pin individual en el mapa para un POI
@Composable
fun PoiPin(
    poi: Poi,
    mode: MapMode,
    isBeingDragged: Boolean,
    onPoiClick: (Poi) -> Unit,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Anima el tamaño del pin cuando se arrastra
    val size by animateDpAsState(
        targetValue = if (isBeingDragged) 50.dp else 40.dp,
        label = "poiSizeAnimation"
    )

    // Convierte el color hexadecimal del POI a un objeto Color
    val poiColor = try {
        Color(android.graphics.Color.parseColor(poi.colorHex))
    } catch (e: IllegalArgumentException) {
        Color.Black // Color de respaldo en caso de error
    }

    Box(
        modifier = modifier
            .pointerInput(poi, mode) {
                // Lógica de gestos para tocar y arrastrar
                if (mode == MapMode.EDIT_POI) {
                    detectTapGestures(
                        onLongPress = { onDragStart() }, // Inicia el arrastre con una pulsación larga
                        onTap = { onPoiClick(poi) }
                    )
                } else {
                    detectTapGestures(onTap = { onPoiClick(poi) })
                }
            }
            .pointerInput(isBeingDragged) {
                if (isBeingDragged) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onDrag(dragAmount)
                        },
                        onDragEnd = { onDragEnd() }
                    )
                }
            }
    ) {
        Icon(
            imageVector = IconProvider.getIcon(poi.iconName),
            contentDescription = "Punto de Interés: ${poi.title}",
            tint = if (isBeingDragged) Color(0xFFF44336) else poiColor, // Cambia de color al arrastrar
            modifier = Modifier.size(size)
        )
    }
}
