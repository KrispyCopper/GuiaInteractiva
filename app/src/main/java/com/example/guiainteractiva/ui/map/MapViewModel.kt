package com.example.guiainteractiva.ui.map

import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
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

// Estado de la interfaz de usuario para la pantalla del mapa
data class MapUiState(
    val mode: MapMode = MapMode.VIEW,
    val pois: List<Poi> = emptyList(),
    val selectedPoi: Poi? = null,
    val isUserAdmin: Boolean = false,
    val isLoadingImage: Boolean = false,
    val scale: Float = 1f,
    val offset: Offset = Offset.Zero,
    val containerSize: IntSize = IntSize.Zero,
    val imageSize: Size = Size.Zero,
    val isInitialized: Boolean = false,
    val isPoiListVisible: Boolean = false
)

class MapViewModel : ViewModel() {

    // Flujo de estado de la UI
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // Servicios de Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val poiRepository = PoiRepository()

    // Bloque de inicialización
    init {
        fetchUserRole()
        listenToPois()
    }

    // Lógica de estado del mapa
    fun onMapSizeChanged(newContainerSize: IntSize, newImageSize: Size) {
        _uiState.update { currentState ->
            if (newContainerSize.width == 0 || newImageSize.width == 0f) {
                return@update currentState.copy(
                    containerSize = newContainerSize,
                    imageSize = newImageSize
                )
            }

            if (!currentState.isInitialized) {
                val scaleX = newContainerSize.width / newImageSize.width
                val scaleY = newContainerSize.height / newImageSize.height
                val initialScale = maxOf(scaleX, scaleY)
                val initialOffset = clampOffset(Offset.Zero, initialScale, newContainerSize, newImageSize)
                currentState.copy(
                    containerSize = newContainerSize,
                    imageSize = newImageSize,
                    scale = initialScale,
                    offset = initialOffset,
                    isInitialized = true
                )
            } else {
                currentState.copy(
                    containerSize = newContainerSize,
                    imageSize = newImageSize,
                    offset = clampOffset(currentState.offset, currentState.scale, newContainerSize, newImageSize)
                )
            }
        }
    }

    // Lógica de transformación del mapa
    fun onMapTransform(centroid: Offset, pan: Offset, zoom: Float) {
        _uiState.update { currentState ->
            if (!currentState.isInitialized) return@update currentState

            val newScale = (currentState.scale * zoom).coerceIn(0.5f, 5f)
            val zoomRatio = newScale / currentState.scale
            val proposedOffset = (currentState.offset - centroid) * zoomRatio + centroid + pan

            currentState.copy(
                scale = newScale,
                offset = clampOffset(proposedOffset, newScale, currentState.containerSize, currentState.imageSize)
            )
        }
    }

    // Lógica de zoom
    fun onZoomIn() {
        handleZoom(1.2f)
    }

    fun onZoomOut() {
        handleZoom(1 / 1.2f)
    }

    private fun handleZoom(zoomFactor: Float) {
        _uiState.update { currentState ->
            if (!currentState.isInitialized) return@update currentState

            val newScale = (currentState.scale * zoomFactor).coerceIn(0.5f, 5f)
            val centroid = Offset(currentState.containerSize.width / 2f, currentState.containerSize.height / 2f)
            val zoomRatio = newScale / currentState.scale
            val proposedOffset = (currentState.offset - centroid) * zoomRatio + centroid

            currentState.copy(
                scale = newScale,
                offset = clampOffset(proposedOffset, newScale, currentState.containerSize, currentState.imageSize)
            )
        }
    }

    // Función para limitar la posición del mapa
    private fun clampOffset(proposedOffset: Offset, scale: Float, containerSize: IntSize, imageSize: Size): Offset {
        if (containerSize.width == 0 || imageSize.width == 0f) return Offset.Zero

        val scaledW = imageSize.width * scale
        val scaledH = imageSize.height * scale

        val newX = if (scaledW > containerSize.width) {
            proposedOffset.x.coerceIn(containerSize.width - scaledW, 0f)
        } else {
            (containerSize.width - scaledW) / 2f
        }

        val newY = if (scaledH > containerSize.height) {
            proposedOffset.y.coerceIn(containerSize.height - scaledH, 0f)
        } else {
            (containerSize.height - scaledH) / 2f
        }
        return Offset(newX, newY)
    }

    // Lógica de la lista de POIs
    fun onTogglePoiList() {
        _uiState.update { it.copy(isPoiListVisible = !it.isPoiListVisible) }
    }

    fun onPoiSelectedFromList(poi: Poi) {
        _uiState.update { currentState ->
            if (!currentState.isInitialized) return@update currentState

            val targetScale = (currentState.scale).coerceAtLeast(2f)
            val targetOffset = Offset(
                x = currentState.containerSize.width / 2f - poi.positionOnImage.x * targetScale,
                y = currentState.containerSize.height / 2f - poi.positionOnImage.y * targetScale
            )

            currentState.copy(
                selectedPoi = poi,
                isPoiListVisible = false,
                scale = targetScale,
                offset = clampOffset(targetOffset, targetScale, currentState.containerSize, currentState.imageSize)
            )
        }
    }

    // Obtención de datos de Firebase
    private fun listenToPois() {
        viewModelScope.launch {
            poiRepository.getAllPois().collect { poiList ->
                _uiState.update { it.copy(pois = poiList) }
            }
        }
    }

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
                _uiState.update { it.copy(isUserAdmin = false) }
            }
        }
    }

    // Lógica de gestión de POIs
    fun onImageSelected(imageUri: Uri) {
        val currentPoi = _uiState.value.selectedPoi ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingImage = true) }
            try {
                val poiId = if (currentPoi.id.isNotBlank()) currentPoi.id else UUID.randomUUID().toString()
                val fileName = "${UUID.randomUUID()}.jpg"
                val imageRef = storage.reference.child("poi_images/$poiId/$fileName")

                imageRef.putFile(imageUri).await()
                val downloadUrl = imageRef.downloadUrl.await().toString()

                _uiState.update { currentState ->
                    currentState.copy(
                        selectedPoi = currentState.selectedPoi?.copy(id = poiId, imageUrl = downloadUrl),
                        isLoadingImage = false
                    )
                }
            } catch (e: Exception) {
                Log.e("MapViewModel", "Error al subir la imagen", e)
                _uiState.update { it.copy(isLoadingImage = false) }
            }
        }
    }

    fun onAddPoi(position: Offset) {
        val newPoi = Poi(title = "", emoji = "", positionOnImage = position)
        _uiState.update { it.copy(selectedPoi = newPoi, mode = MapMode.EDIT_POI) }
    }

    fun onDeletePoi(poiToDelete: Poi) {
        viewModelScope.launch {
            poiRepository.deletePoi(poiToDelete.id)
            _uiState.update { it.copy(mode = MapMode.VIEW, selectedPoi = null) }
        }
    }

    fun onPoiClicked(poi: Poi) {
        _uiState.update { it.copy(selectedPoi = poi, isLoadingImage = false) }
    }

    fun onPoiMoved(poi: Poi, newPosition: Offset) {
        viewModelScope.launch {
            val updatedPoi = poi.copy(positionX = newPosition.x.toDouble(), positionY = newPosition.y.toDouble())
            poiRepository.updatePoi(updatedPoi)
        }
    }

    // Lógica de edición del panel de detalles
    fun onPoiTitleChanged(newTitle: String) {
        _uiState.update { it.copy(selectedPoi = it.selectedPoi?.copy(title = newTitle)) }
    }

    fun onEmojiChanged(newEmoji: String) {
        _uiState.update { it.copy(selectedPoi = it.selectedPoi?.copy(emoji = newEmoji)) }
    }

    fun onPoiDescriptionChanged(newDescription: String) {
        _uiState.update { it.copy(selectedPoi = it.selectedPoi?.copy(description = newDescription)) }
    }

    fun onIconChanged(newIconName: String) {
        _uiState.update { it.copy(selectedPoi = it.selectedPoi?.copy(iconName = newIconName)) }
    }

    fun onColorChanged(newColorHex: String) {
        _uiState.update { it.copy(selectedPoi = it.selectedPoi?.copy(colorHex = newColorHex)) }
    }

    fun onConfirmChanges() {
        _uiState.value.selectedPoi?.let { poiToSave ->
            viewModelScope.launch {
                if (poiToSave.id.isBlank()) {
                    poiRepository.addPoi(poiToSave)
                } else {
                    poiRepository.updatePoi(poiToSave)
                }
                _uiState.update { it.copy(selectedPoi = null, isLoadingImage = false, mode = MapMode.VIEW) }
            }
        }
    }

    fun onCancelChanges() {
        _uiState.update { it.copy(selectedPoi = null, isLoadingImage = false, mode = MapMode.VIEW) }
    }

    // Lógica de cambio de modo
    fun onEnterAddPoiMode() {
        _uiState.update { it.copy(mode = MapMode.ADD_POI) }
    }

    fun onEnterDeletePoiMode() {
        _uiState.update { it.copy(mode = MapMode.DELETE_POI) }
    }

    fun onEnterEditMode() {
        _uiState.update { it.copy(mode = MapMode.EDIT_POI) }
    }

    fun onEnterViewMode() {
        _uiState.update { it.copy(mode = MapMode.VIEW) }
    }
}
