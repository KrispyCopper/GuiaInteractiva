package com.example.guiainteractiva.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.guiainteractiva.model.Poi
import com.example.guiainteractiva.ui.theme.DarkGrayField
import com.example.guiainteractiva.ui.theme.GrayField


    //Panel inferior que muestra la información de un POI para el usuario (solo lectura).
@Composable
fun UserBottomPanel(
    modifier: Modifier = Modifier,
    selectedPoi: Poi?
) {
    //si no tenemos un POI seleccionado muestra este texto
    if (selectedPoi == null) {
        Column(
            modifier = modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Información")
            Text("Selecciona un punto en el mapa para ver su detalle.")
        }
        return
    }

    // Si hay un POI, muestra su información.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TitleRow(emoji = selectedPoi.emoji, title = selectedPoi.title)

        // La descripción solo se muestra si no está vacía.
        if (selectedPoi.description.isNotBlank()) {
            DescriptionBody(description = selectedPoi.description)
        }

        // Si el POI tiene una imagen, la mostramos.
        if (!selectedPoi.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = selectedPoi.imageUrl,
                contentDescription = "Imagen de ${selectedPoi.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun TitleRow(emoji: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (emoji.isNotBlank()) {
            Card(
                modifier = Modifier.width(70.dp).height(56.dp), // Mismo alto que un TextField estándar
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme()) DarkGrayField else GrayField)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}
@Composable
private fun DescriptionBody(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = description,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
