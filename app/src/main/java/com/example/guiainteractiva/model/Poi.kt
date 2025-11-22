package com.example.guiainteractiva.model

import androidx.compose.ui.geometry.Offset
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude


    //Modelo de datos para un Punto de Interés (POI).
    //NOTA: Firestore necesita un constructor sin argumentos y propiedades públicas (vars).

data class Poi(
    @DocumentId
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var emoji: String = "",

    // Guardamos las coordenadas X e Y como Doubles, ya que Firestore no entiende el tipo Offset.
    var positionX: Double = 0.0,
    var positionY: Double = 0.0
) {
    // Usamos @Exclude para que Firestore ignore este getter al (de)serializar.
    // Esto nos permite seguir usando el tipo Offset en el resto de la app de forma cómoda.
    @get:Exclude
    val positionOnImage: Offset
        get() = Offset(positionX.toFloat(), positionY.toFloat())

    // Constructor secundario para facilitar la creación de un POI a partir de un Offset.
    constructor(
        id: String = "",
        title: String,
        description: String = "",
        emoji: String,
        positionOnImage: Offset
    ) : this(
        id = id,
        title = title,
        description = description,
        emoji = emoji,
        positionX = positionOnImage.x.toDouble(),
        positionY = positionOnImage.y.toDouble()
    )
}
