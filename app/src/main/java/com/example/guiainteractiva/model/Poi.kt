package com.example.guiainteractiva.model

import androidx.compose.ui.geometry.Offset
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

// Modelo de datos para un Punto de Interés (POI)
data class Poi(
    // ID del documento en Firestore
    @DocumentId
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var emoji: String = "",
    var imageUrl: String? = null,
    var iconName: String = "location_on",
    var colorHex: String = "#4CAF50",

    // Coordenadas de posición en la imagen
    var positionX: Double = 0.0,
    var positionY: Double = 0.0
) {
    // Propiedad calculada para obtener la posición como un objeto Offset
    @get:Exclude
    val positionOnImage: Offset
        get() = Offset(positionX.toFloat(), positionY.toFloat())

    // Constructor secundario para mayor conveniencia al crear un POI
    constructor(
        id: String = "",
        title: String,
        description: String = "",
        emoji: String,
        imageUrl: String? = null,
        iconName: String = "location_on",
        colorHex: String = "#4CAF50",
        positionOnImage: Offset
    ) : this(
        id = id,
        title = title,
        description = description,
        emoji = emoji,
        imageUrl = imageUrl,
        iconName = iconName,
        colorHex = colorHex,
        positionX = positionOnImage.x.toDouble(),
        positionY = positionOnImage.y.toDouble()
    )
}
