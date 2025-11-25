package com.example.guiainteractiva.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.guiainteractiva.model.MapMode
import com.example.guiainteractiva.ui.components.AdminBottomPanel
import com.example.guiainteractiva.ui.components.BackButton
import com.example.guiainteractiva.ui.components.PoiActionButtons
import com.example.guiainteractiva.ui.components.ProfileButton
import com.example.guiainteractiva.ui.components.UserBottomPanel
import com.example.guiainteractiva.ui.components.ZoomControls
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isUserAdmin) {
        AdminMapScreen(mapViewModel, onNavigateBack)
    } else {
        UserMapScreen(mapViewModel, onNavigateBack)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMapScreen(
    mapViewModel: MapViewModel,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    var zoomFactor by remember { mutableFloatStateOf(1f) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (uiState.selectedPoi != null) 64.dp else 0.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent = {
            AdminBottomPanel(
                selectedPoi = uiState.selectedPoi,
                isLoadingImage = uiState.isLoadingImage,
                onTitleChange = { mapViewModel.onPoiTitleChanged(it) },
                onDescriptionChange = { mapViewModel.onPoiDescriptionChanged(it) },
                onEmojiChange = { mapViewModel.onEmojiChanged(it) },
                onImageSelected = { mapViewModel.onImageSelected(it) },
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
                zoomFactor = zoomFactor,
                onZoomFinished = { zoomFactor = 1f },
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
                onZoomIn = { zoomFactor = 1.2f },
                onZoomOut = { zoomFactor = 1 / 1.2f },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 96.dp, end = 16.dp)
            )

            BackButton(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                onClick = {
                    if (uiState.mode != MapMode.VIEW) {
                        mapViewModel.onEnterViewMode()
                    } else {
                        onNavigateBack()
                    }
                }
            )

            ProfileButton(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp))

            AnimatedVisibility(
                visible = uiState.mode == MapMode.VIEW,
                modifier = Modifier.align(Alignment.CenterStart).offset(y = (-80).dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMapScreen(
    mapViewModel: MapViewModel,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val uiState by mapViewModel.uiState.collectAsStateWithLifecycle()
    var zoomFactor by remember { mutableFloatStateOf(1f) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (uiState.selectedPoi != null) 64.dp else 0.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent = { 
            UserBottomPanel(selectedPoi = uiState.selectedPoi)
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            ZoomableMapContent(
                modifier = Modifier.fillMaxSize(),
                mode = uiState.mode,
                pois = uiState.pois,
                zoomFactor = zoomFactor,
                onZoomFinished = { zoomFactor = 1f },
                onAddPoi = { /* No-op */ },
                onPoiClick = { poi ->
                    mapViewModel.onPoiClicked(poi)
                    scope.launch { scaffoldState.bottomSheetState.expand() }
                }
            )

            ZoomControls(
                onZoomIn = { zoomFactor = 1.2f },
                onZoomOut = { zoomFactor = 1 / 1.2f },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 96.dp, end = 16.dp)
            )

            BackButton(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                onClick = onNavigateBack
            )

            ProfileButton(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp))
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
