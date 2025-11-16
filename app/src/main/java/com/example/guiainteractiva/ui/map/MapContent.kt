package com.example.guiainteractiva.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ZoomableMapContent(
    modifier: Modifier = Modifier,
    mode: MapMode,
    pois: List<Poi>,
    onAddPoi: (Offset) -> Unit,
    onPoiClick: (Poi) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var minScale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current
    val painter = painterResource(id = R.drawable.map_placeholder)

    var hasInitialized by remember { mutableStateOf(false) }

    // INICIALIZACIÓN UNA SOLA VEZ (centrar el mapa correctamente, Dios que dificil fue lograr
    // Que el mapa se quedara bien centrado, estuve toda una tarde intentando que quedara bien)

    LaunchedEffect(containerSize) {
        if (hasInitialized) return@LaunchedEffect
        if (containerSize.width == 0 || containerSize.height == 0) return@LaunchedEffect
        if (painter.intrinsicSize.width <= 0f || painter.intrinsicSize.height <= 0f) return@LaunchedEffect

        val imgW = painter.intrinsicSize.width
        val imgH = painter.intrinsicSize.height

        // Escala inicial para cubrir la pantalla
        val scaleX = containerSize.width / imgW
        val scaleY = containerSize.height / imgH
        val initialScale = max(scaleX, scaleY)

        scale = initialScale
        minScale = initialScale

        // Centrado horizontal, pegado arriba (sin espacio blanco)
        val scaledW = imgW * initialScale

        offset = Offset(
            x = (containerSize.width - scaledW) / 2f,
            y = 0f
        )

        hasInitialized = true
    }

    // GESTOS DE ZOOM + PAN
    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .onSizeChanged { containerSize = it }
            .pointerInput(mode) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    if (mode == MapMode.VIEW || mode == MapMode.DELETE_POI) {
                        val oldScale = scale
                        val newScale = (scale * zoom).coerceIn(minScale, minScale * 5f)

                        var newOffset =
                            (offset - centroid) * (newScale / oldScale) + centroid + pan

                        val imgW = painter.intrinsicSize.width * newScale
                        val imgH = painter.intrinsicSize.height * newScale

                        val minX = (containerSize.width - imgW).coerceAtMost(0f)
                        val maxX = 0f
                        val minY = (containerSize.height - imgH).coerceAtMost(0f)
                        val maxY = 0f

                        newOffset = Offset(
                            x = newOffset.x.coerceIn(minX, maxX),
                            y = newOffset.y.coerceIn(minY, maxY)
                        )

                        scale = newScale
                        offset = newOffset
                    }
                }
            }
            .pointerInput(mode) {
                if (mode == MapMode.ADD_POI) {
                    detectTapGestures { tap ->
                        val imgPos = (tap - offset) / scale

                        if (
                            imgPos.x in 0f..painter.intrinsicSize.width &&
                            imgPos.y in 0f..painter.intrinsicSize.height
                        ) {
                            onAddPoi(imgPos)
                        }
                    }
                }
            }
    ) {

        Image(
            painter = painter,
            contentDescription = "Mapa del museo",
            contentScale = ContentScale.None,
            modifier = Modifier
                .onSizeChanged { /* evita tamaño 0x0 */ }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        )
        // POIs (completamente alineados al mapa)
        pois.forEach { poi ->
            PoiPin(
                poi = poi,
                onPoiClick = onPoiClick,
                modifier = Modifier.offset {
                    with(density) {
                        val pos = poi.positionOnImage * scale + offset
                        val markerPx = 48.dp.toPx()

                        IntOffset(
                            (pos.x - markerPx / 2).roundToInt(),
                            (pos.y - markerPx / 2).roundToInt()
                        )
                    }
                }
            )
        }
    }
}
