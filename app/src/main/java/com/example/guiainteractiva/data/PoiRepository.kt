package com.example.guiainteractiva.data

import com.example.guiainteractiva.model.Poi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

// Repositorio para gestionar los Puntos de Interés (POIs) en Firestore
class PoiRepository {

    // Instancia de Firestore y referencia a la colección
    private val db = FirebaseFirestore.getInstance()
    private val poiCollection = db.collection("pois")

    // Obtiene un flujo de todos los POIs
    fun getAllPois(): Flow<List<Poi>> {
        return poiCollection.snapshots().map { snapshot ->
            snapshot.toObjects<Poi>()
        }
    }

    // Añade un nuevo POI a Firestore
    suspend fun addPoi(poi: Poi) {
        poiCollection.add(poi).await()
    }

    // Actualiza un POI existente en Firestore
    suspend fun updatePoi(poi: Poi) {
        if (poi.id.isNotBlank()) {
            poiCollection.document(poi.id).set(poi).await()
        }
    }

    // Elimina un POI de Firestore
    suspend fun deletePoi(poiId: String) {
        if (poiId.isNotBlank()) {
            poiCollection.document(poiId).delete().await()
        }
    }
}
