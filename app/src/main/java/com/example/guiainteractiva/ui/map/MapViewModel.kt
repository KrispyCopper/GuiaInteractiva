package com.example.guiainteractiva.ui.map

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiainteractiva.data.PoiRepository
import com.example.guiainteractiva.model.MapMode
import com.example.guiainteractiva.model.Poi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

// Estado de la UI para la pantalla del mapa
data class MapUiState(
    val mode: MapMode = MapMode.VIEW,
    val pois: List<Poi> = emptyList(),
    val selectedPoi: Poi? = null,
    val isUserAdmin: Boolean = false
)

class MapViewModel : ViewModel() {

    // Flujo de estado para la UI, privado para el ViewModel
    private val _uiState = MutableStateFlow(MapUiState())
    // Flujo de estado público y de solo lectura para la UI
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // Dependencias de Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    // Repositorio para la lógica de datos de los POIs
    private val poiRepository = PoiRepository()

    // Bloque de inicialización
    init {
        fetchUserRole()
        listenToPois()
    }

    // Escucha los cambios en los POIs en tiempo real
    private fun listenToPois() {
        viewModelScope.launch {
            poiRepository.getAllPois().collect { poiList ->
                _uiState.update { it.copy(pois = poiList) }
            }
        }
    }

    // Obtiene el rol del usuario para determinar si es admin, usando coroutines
    private fun fetchUserRole() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    _uiState.update { it.copy(isUserAdmin = false) }
                    return@launch
                }

                val document = db.collection("users").document(userId).get().await()
                val role = document.getString("role")
                _uiState.update { it.copy(isUserAdmin = role == "admin") }

            } catch (e: Exception) {
                // Si hay algún error (ej: sin conexión), asumimos que no es admin.
                _uiState.update { it.copy(isUserAdmin = false) }
            }
        }
    }

    // Añade un nuevo POI
    fun onAddPoi(position: Offset) {
        viewModelScope.launch {
            val newPoi = Poi(title = "Nuevo Punto", emoji = "📍", positionOnImage = position)
            poiRepository.addPoi(newPoi) // Añadimos a Firestore
            // El resto se actualiza solo gracias al listener
            _uiState.update { it.copy(mode = MapMode.VIEW) }
        }
    }

    // Elimina un POI
    fun onDeletePoi(poiToDelete: Poi) {
        viewModelScope.launch {
            poiRepository.deletePoi(poiToDelete.id) // Borramos de Firestore
            _uiState.update { it.copy(mode = MapMode.VIEW) }
        }
    }

    // Selecciona un POI para mostrar sus detalles
    fun onPoiClicked(poi: Poi) {
        _uiState.update { it.copy(selectedPoi = poi) }
    }

    // Actualiza el título en el estado local
    fun onPoiTitleChanged(newTitle: String) {
        _uiState.update {
            it.copy(selectedPoi = it.selectedPoi?.copy(title = newTitle))
        }
    }

    // Actualiza el emoji en el estado local
    fun onEmojiChanged(newEmoji: String) {
        _uiState.update {
            it.copy(selectedPoi = it.selectedPoi?.copy(emoji = newEmoji))
        }
    }

    // Actualiza la descripción en el estado local
    fun onPoiDescriptionChanged(newDescription: String) {
        _uiState.update {
            it.copy(selectedPoi = it.selectedPoi?.copy(description = newDescription))
        }
    }

    // Confirma y guarda los cambios en la base de datos
    fun onConfirmChanges() {
        _uiState.value.selectedPoi?.let { updatedPoi ->
            viewModelScope.launch {
                poiRepository.updatePoi(updatedPoi)
                _uiState.update { it.copy(selectedPoi = null) }
            }
        }
    }

    // Cancela la edición y cierra el panel
    fun onCancelChanges() {
        _uiState.update { it.copy(selectedPoi = null) }
    }

    // Entra en modo "Añadir POI"
    fun onEnterAddPoiMode() {
        _uiState.update { it.copy(mode = MapMode.ADD_POI) }
    }

    // Entra en modo "Eliminar POI"
    fun onEnterDeletePoiMode() {
        _uiState.update { it.copy(mode = MapMode.DELETE_POI) }
    }

    // Vuelve al modo de visualización
    fun onEnterViewMode() {
        _uiState.update { it.copy(mode = MapMode.VIEW) }
    }
}
