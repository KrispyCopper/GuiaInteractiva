package com.example.guiainteractiva.ui.map

import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guiainteractiva.data.PoiRepository
import com.example.guiainteractiva.model.MapMode
import com.example.guiainteractiva.model.Poi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

// Estado de la UI para la pantalla del mapa
data class MapUiState(
    val mode: MapMode = MapMode.VIEW,
    val pois: List<Poi> = emptyList(),
    val selectedPoi: Poi? = null,
    val isUserAdmin: Boolean = false,
    val isLoadingImage: Boolean = false
)

class MapViewModel : ViewModel() {

    // Flujo de estado para la UI, privado para el ViewModel
    private val _uiState = MutableStateFlow(MapUiState())
    // Flujo de estado público y de solo lectura para la UI
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // Dependencias de Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

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

    //Gestión de Imágenes

    fun onImageSelected(imageUri: Uri) {
        val currentPoi = _uiState.value.selectedPoi
        // Comprobación de seguridad: No subir imagen sin un POI con ID válido
        if (currentPoi == null || currentPoi.id.isBlank()) {
            Log.e("MapViewModel", "Intento de subir imagen sin un POI válido seleccionado.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingImage = true) }
            try {
                val fileName = "${UUID.randomUUID()}.jpg"
                val imageRef = storage.reference.child("poi_images/${currentPoi.id}/$fileName")

                imageRef.putFile(imageUri).await()
                val downloadUrl = imageRef.downloadUrl.await().toString()

                _uiState.update { currentState ->
                    currentState.copy(
                        selectedPoi = currentState.selectedPoi?.copy(imageUrl = downloadUrl),
                        isLoadingImage = false
                    )
                }
            } catch (e: Exception) {
                // Añadimos un log para poder depurar el error exacto en Logcat
                Log.e("MapViewModel", "Error al subir la imagen a Firebase Storage", e)
                _uiState.update { it.copy(isLoadingImage = false) }
            }
        }
    }

    //Gestión de POIs

    // Añade un nuevo POI
    fun onAddPoi(position: Offset) {
        viewModelScope.launch {
            val newPoi = Poi(title = "Nuevo Punto", emoji = "📍", positionOnImage = position)
            poiRepository.addPoi(newPoi)
            _uiState.update { it.copy(mode = MapMode.VIEW) }
        }
    }

    // Elimina un POI
    fun onDeletePoi(poiToDelete: Poi) {
        viewModelScope.launch {
            poiRepository.deletePoi(poiToDelete.id)
            _uiState.update { it.copy(mode = MapMode.VIEW) }
        }
    }

    // Selecciona un POI para mostrar sus detalles
    fun onPoiClicked(poi: Poi) {
        _uiState.update { it.copy(selectedPoi = poi, isLoadingImage = false) }
    }

    //Edición Temporal del POI Seleccionado
    fun onPoiTitleChanged(newTitle: String) {
        _uiState.update {
            it.copy(selectedPoi = it.selectedPoi?.copy(title = newTitle))
        }
    }

    fun onEmojiChanged(newEmoji: String) {
        _uiState.update {
            it.copy(selectedPoi = it.selectedPoi?.copy(emoji = newEmoji))
        }
    }

    fun onPoiDescriptionChanged(newDescription: String) {
        _uiState.update {
            it.copy(selectedPoi = it.selectedPoi?.copy(description = newDescription))
        }
    }

    //Acciones de Confirmación
    fun onConfirmChanges() {
        _uiState.value.selectedPoi?.let { updatedPoi ->
            viewModelScope.launch {
                poiRepository.updatePoi(updatedPoi) // Esto ahora guarda también la imageUrl
                _uiState.update { it.copy(selectedPoi = null, isLoadingImage = false) }
            }
        }
    }

    fun onCancelChanges() {
        _uiState.update { it.copy(selectedPoi = null, isLoadingImage = false) }
    }

    //Gestión de Modos de la UI
    fun onEnterAddPoiMode() {
        _uiState.update { it.copy(mode = MapMode.ADD_POI) }
    }

    fun onEnterDeletePoiMode() {
        _uiState.update { it.copy(mode = MapMode.DELETE_POI) }
    }

    fun onEnterViewMode() {
        _uiState.update { it.copy(mode = MapMode.VIEW) }
    }
}
