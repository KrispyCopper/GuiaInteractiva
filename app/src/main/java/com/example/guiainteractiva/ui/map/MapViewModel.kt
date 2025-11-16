package com.example.guiainteractiva.ui.map

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.example.guiainteractiva.model.MapMode
import com.example.guiainteractiva.model.Poi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Estado principal que usa la pantalla del mapa
data class MapUiState(
    val mode: MapMode = MapMode.VIEW,
    val pois: List<Poi> = emptyList(),
    val selectedPoi: Poi? = null
)

// ViewModel encargado de manejar POIs y modos del mapa
class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // Crear un POI nuevo
    fun onAddPoi(position: Offset) {
        val newPoi = Poi(positionOnImage = position, title = "Nuevo Punto")
        _uiState.update { state ->
            state.copy(
                pois = state.pois + newPoi,
                selectedPoi = newPoi,
                mode = MapMode.VIEW
            )
        }
    }

    // Eliminar un POI existente
    fun onDeletePoi(poiToDelete: Poi) {
        _uiState.update { state ->
            state.copy(
                pois = state.pois.filter { it.id != poiToDelete.id },
                mode = MapMode.VIEW
            )
        }
    }

    // Seleccionar un POI para editarlo
    fun onPoiClicked(poi: Poi) {
        _uiState.update { it.copy(selectedPoi = poi) }
    }

    // Cambiar título del POI seleccionado
    fun onPoiTitleChanged(newTitle: String) {
        _uiState.update {
            it.copy(selectedPoi = it.selectedPoi?.copy(title = newTitle))
        }
    }

    // Cambiar descripción del POI seleccionado
    fun onPoiDescriptionChanged(newDescription: String) {
        _uiState.update {
            it.copy(selectedPoi = it.selectedPoi?.copy(description = newDescription))
        }
    }

    // Guardar cambios del panel
    fun onConfirmChanges() {
        _uiState.value.selectedPoi?.let { updatedPoi ->
            _uiState.update { state ->
                state.copy(
                    pois = state.pois.map { poi ->
                        if (poi.id == updatedPoi.id) updatedPoi else poi
                    },
                    selectedPoi = null
                )
            }
        }
    }

    // Cancelar edición (solo cierra el panel)
    fun onCancelChanges() {
        _uiState.update { it.copy(selectedPoi = null) }
    }

    // Modo: añadir POIs
    fun onEnterAddPoiMode() {
        _uiState.update { it.copy(mode = MapMode.ADD_POI) }
    }

    // Modo: borrar POIs
    fun onEnterDeletePoiMode() {
        _uiState.update { it.copy(mode = MapMode.DELETE_POI) }
    }

    // Modo vista normal
    fun onEnterViewMode() {
        _uiState.update { it.copy(mode = MapMode.VIEW) }
    }
}
