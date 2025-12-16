package com.example.guiainteractiva.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.guiainteractiva.R
import com.example.guiainteractiva.model.MapMode
import com.example.guiainteractiva.model.Poi
import com.example.guiainteractiva.ui.components.ZoomControls
import kotlin.math.roundToInt

// Contenido principal del mapa, con lógica de zoom y gestos
@Composable
fun ZoomableMapContent(
    modifier: Modifier = Modifier,
    scale: Float,
    offset: Offset,
    mode: MapMode,
    pois: List<Poi>,
    onTransform: (centroid: Offset, pan: Offset, zoom: Float) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onSizeChanged: (size: IntSize) -> Unit,
    onAddPoi: (Offset) -> Unit,
    onPoiClick: (Poi) -> Unit,
    onPoiMoved: (Poi, Offset) -> Unit
) {
    var draggedPoi by remember { mutableStateOf<Poi?>(null) }
    val density = LocalDensity.current
    val painter = painterResource(id = R.drawable.map_daytime)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .onSizeChanged { onSizeChanged(it) }
            .pointerInput(Unit) {
                // Detecta gestos de zoom y arrastre del mapa
                detectTransformGestures { centroid, pan, zoom, _ ->
                    onTransform(centroid, pan, zoom)
                }
            }
            .pointerInput(mode) {
                // Detecta toques para añadir POIs si está en el modo correcto
                if (mode == MapMode.ADD_POI) {
                    detectTapGestures { tap ->
                        val imgPos = (tap - offset) / scale
                        onAddPoi(imgPos)
                    }
                }
            }
    ) {
        // Imagen del mapa
        Image(
            painter = painter,
            contentDescription = "Mapa",
            contentScale = ContentScale.None,
            alignment = Alignment.TopStart,
            modifier = Modifier
                .wrapContentSize(unbounded = true, align = Alignment.TopStart)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        )

        // Dibuja los pines de los POIs
        pois.forEach { poi ->
            val isBeingDragged = draggedPoi?.id == poi.id
            var currentPos by remember(poi.id) { mutableStateOf(poi.positionOnImage) }

            // Sincroniza la posición si cambia desde fuera
            // mientras no se está arrastrando este pin en concreto.
            LaunchedEffect(poi.positionOnImage) {
                if (!isBeingDragged) {
                    currentPos = poi.positionOnImage
                }
            }

            val pinOffset = (currentPos * scale + offset)
            val pinSizePx = with(density) { 40.dp.toPx() }

            PoiPin(
                poi = poi,
                mode = mode,
                isBeingDragged = isBeingDragged,
                onPoiClick = { if (!isBeingDragged) onPoiClick(it) },
                onDragStart = { draggedPoi = poi },
                onDrag = { dragAmount -> currentPos += dragAmount / scale },
                onDragEnd = {
                    onPoiMoved(poi, currentPos)
                    draggedPoi = null
                },
                modifier = Modifier.offset {
                    IntOffset(
                        (pinOffset.x - pinSizePx / 2).roundToInt(),
                        (pinOffset.y - pinSizePx / 2).roundToInt()
                    )
                }
            )
        }

        // Controles de zoom
        ZoomControls(
            onZoomIn = onZoomIn,
            onZoomOut = onZoomOut,
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 96.dp, end = 16.dp)
        )
    }
}
