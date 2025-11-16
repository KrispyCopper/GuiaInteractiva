package com.example.guiainteractiva.model

import androidx.compose.ui.geometry.Offset
import java.util.UUID

data class Poi(
    val id: UUID = UUID.randomUUID(),
    val positionOnImage: Offset,
    val title: String = "",
    val description: String = ""
)
