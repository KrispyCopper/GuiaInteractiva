package com.example.guiainteractiva.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guiainteractiva.model.MapMode
import com.example.guiainteractiva.ui.components.AdminBottomPanel
import com.example.guiainteractiva.ui.components.BackButton
import com.example.guiainteractiva.ui.components.PoiActionButtons
import com.example.guiainteractiva.ui.components.ProfileButton
import com.example.guiainteractiva.ui.theme.GuiaInteractivaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (uiState.selectedPoi != null) 64.dp else 0.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent = {
            AdminBottomPanel(
                selectedPoi = uiState.selectedPoi,
                onTitleChange = { mapViewModel.onPoiTitleChanged(it) },
                onDescriptionChange = { mapViewModel.onPoiDescriptionChanged(it) },
                onEmojiChange = { mapViewModel.onEmojiChanged(it) }, // Se añade el parámetro que faltaba
                onConfirm = {
                    mapViewModel.onConfirmChanges()
                    scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                },
                onCancel = {
                    mapViewModel.onCancelChanges()
                    scope.launch { scaffoldState.bottomSheetState.partialExpand() }
                }
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            ZoomableMapContent(
                modifier = Modifier.fillMaxSize(),
                mode = uiState.mode,
                pois = uiState.pois,
                onAddPoi = { position ->
                    mapViewModel.onAddPoi(position)
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                },
                onPoiClick = { poi ->
                    if (uiState.mode == MapMode.DELETE_POI) {
                        mapViewModel.onDeletePoi(poi)
                    } else {
                        mapViewModel.onPoiClicked(poi)
                        scope.launch { scaffoldState.bottomSheetState.expand() }
                    }
                }
            )

            ZoomControls(
                onZoomIn = { /* mapViewModel.onZoomIn() */ }, // Descomenta cuando tengas la función
                onZoomOut = { /* mapViewModel.onZoomOut() */ }, // Descomenta cuando tengas la función
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 96.dp, end = 16.dp) // Ajustado para no chocar con el "HelpText"
            )

            // UI Estática
            BackButton(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                onClick = {
                    if (uiState.mode == MapMode.ADD_POI || uiState.mode == MapMode.DELETE_POI) {
                        mapViewModel.onEnterViewMode()
                    } else {
                        onNavigateBack()
                    }
                }
            )

            ProfileButton(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp))

            AnimatedVisibility(
                visible = uiState.mode == MapMode.VIEW,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(y = (-80).dp)
            ) {
                PoiActionButtons(
                    modifier = Modifier.padding(start = 16.dp),
                    onAddClick = { mapViewModel.onEnterAddPoiMode() },
                    onDeleteClick = { mapViewModel.onEnterDeletePoiMode() }
                )
            }

            AnimatedVisibility(
                visible = uiState.mode == MapMode.ADD_POI,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                HelpText("Toca el mapa para añadir un punto")
            }

            AnimatedVisibility(
                visible = uiState.mode == MapMode.DELETE_POI,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                HelpText("Toca un punto para eliminarlo")
            }
        }
    }
}

@Composable
private fun ZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre los botones
    ) {
        // Botón de Zoom In (+)
        SmallFloatingActionButton(
            onClick = onZoomIn,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Zoom In"
            )
        }

        // Botón de Zoom Out (-)
        SmallFloatingActionButton(
            onClick = onZoomOut,
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Zoom Out"
            )
        }
    }
}

@Composable
private fun HelpText(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(bottom = 96.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    GuiaInteractivaTheme {
    }
}
