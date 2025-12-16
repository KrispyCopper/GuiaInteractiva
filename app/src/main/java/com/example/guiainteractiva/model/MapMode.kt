package com.example.guiainteractiva.model

// Define los modos de interacción del usuario en la pantalla del mapa
enum class MapMode {
    VIEW,       // Modo de solo visualización
    ADD_POI,    // Modo para añadir un nuevo punto
    DELETE_POI, // Modo para eliminar un punto existente
    EDIT_POI    // Modo para editar o mover un punto
}
