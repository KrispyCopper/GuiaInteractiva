package com.example.guiainteractiva.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guiainteractiva.R
import com.example.guiainteractiva.model.MapMode
import com.example.guiainteractiva.ui.components.AdminBottomActionBar
import com.example.guiainteractiva.ui.components.AdminBottomPanel
import com.example.guiainteractiva.ui.components.BackButton
import com.example.guiainteractiva.ui.components.PoiList
import com.example.guiainteractiva.ui.components.UserBottomActionBar
import com.example.guiainteractiva.ui.components.UserBottomPanel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    onNavigateToSettings: () -> Unit,
    onSignOut: () -> Unit
) {
    // Inicialización de estado
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Lógica del panel de detalles
    LaunchedEffect(uiState.selectedPoi) {
        if (uiState.selectedPoi != null) {
            scope.launch { sheetState.show() }
        } else {
            scope.launch { sheetState.hide() }
        }
    }

    // Estructura de la pantalla
    Scaffold(
        bottomBar = { },
        containerColor = Color.Transparent
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Carga de la imagen del mapa
            val painter = painterResource(id = R.drawable.map_daytime)
            val imageSize = remember(painter) { Size(painter.intrinsicSize.width.toFloat(), painter.intrinsicSize.height.toFloat()) }

            // Guardia de carga del mapa
            if (imageSize != Size.Zero) {
                // Contenido del mapa
                ZoomableMapContent(
                    modifier = Modifier.fillMaxSize(),
                    scale = uiState.scale,
                    offset = uiState.offset,
                    mode = uiState.mode,
                    pois = uiState.pois,
                    onTransform = { centroid, pan, zoom -> mapViewModel.onMapTransform(centroid, pan, zoom) },
                    onZoomIn = mapViewModel::onZoomIn,
                    onZoomOut = mapViewModel::onZoomOut,
                    onSizeChanged = { containerSize -> mapViewModel.onMapSizeChanged(containerSize, imageSize) },
                    onAddPoi = { mapViewModel.onAddPoi(it) },
                    onPoiClick = { poi ->
                        if (uiState.mode == MapMode.DELETE_POI) {
                            mapViewModel.onDeletePoi(poi)
                        } else {
                            mapViewModel.onPoiClicked(poi)
                        }
                    },
                    onPoiMoved = { poi, newPosition -> mapViewModel.onPoiMoved(poi, newPosition) }
                )

                // Botón de volver
                BackButton(
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                    onClick = onSignOut
                )

                // Barra de acción inferior
                if (uiState.isUserAdmin) {
                    AdminBottomActionBar(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                        onPoiListClick = { mapViewModel.onTogglePoiList() },
                        onSettingsClick = onNavigateToSettings,
                        onAddClick = { mapViewModel.onEnterAddPoiMode() },
                        onEditClick = { mapViewModel.onEnterEditMode() },
                        onDeleteClick = { mapViewModel.onEnterDeletePoiMode() }
                    )
                } else {
                    UserBottomActionBar(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                        onPoiListClick = { mapViewModel.onTogglePoiList() },
                        onSettingsClick = onNavigateToSettings
                    )
                }

                // Texto de ayuda contextual
                AnimatedVisibility(
                    visible = uiState.mode != MapMode.VIEW,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 96.dp)
                ) {
                    val helpText = when (uiState.mode) {
                        MapMode.ADD_POI -> "Toca el mapa para añadir un punto"
                        MapMode.DELETE_POI -> "Toca un punto para eliminarlo"
                        MapMode.EDIT_POI -> "Toca un punto para editarlo o arrástralo para moverlo"
                        else -> ""
                    }
                    HelpText(helpText)
                }
            } else {
                // Indicador de carga
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Panel lateral de la lista de POIs
            AnimatedVisibility(
                visible = uiState.isPoiListVisible,
                enter = slideInHorizontally() + fadeIn(),
                exit = slideOutHorizontally() + fadeOut()
            ) {
                PoiList(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(250.dp)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)),
                    pois = uiState.pois,
                    onPoiClick = { poi -> mapViewModel.onPoiSelectedFromList(poi) }
                )
            }
        }
    }

    // Panel de detalles del POI
    if (sheetState.isVisible && uiState.selectedPoi != null) {
        ModalBottomSheet(
            onDismissRequest = { mapViewModel.onCancelChanges() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            if (uiState.isUserAdmin) {
                AdminBottomPanel(
                    selectedPoi = uiState.selectedPoi,
                    isLoadingImage = uiState.isLoadingImage,
                    onTitleChange = { mapViewModel.onPoiTitleChanged(it) },
                    onDescriptionChange = { mapViewModel.onPoiDescriptionChanged(it) },
                    onEmojiChange = { mapViewModel.onEmojiChanged(it) },
                    onIconSelected = { mapViewModel.onIconChanged(it) },
                    onColorSelected = { mapViewModel.onColorChanged(it) },
                    onImageSelected = { mapViewModel.onImageSelected(it) },
                    onConfirm = { mapViewModel.onConfirmChanges() },
                    onCancel = { mapViewModel.onCancelChanges() }
                )
            } else {
                UserBottomPanel(selectedPoi = uiState.selectedPoi)
            }
        }
    }
}

// Composable de ayuda
@Composable
private fun HelpText(text: String) {
    if (text.isNotBlank()) {
        Text(
            text = text,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 18.dp, vertical = 10.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
