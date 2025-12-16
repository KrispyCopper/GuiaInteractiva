package com.example.guiainteractiva.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
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

// Panel de detalles del POI para usuarios no administradores
@Composable
fun UserBottomPanel(modifier: Modifier = Modifier, selectedPoi: Poi?) {
    if (selectedPoi == null) {
        // Mensaje de bienvenida si no hay ningún POI seleccionado
        Column(
            modifier = modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Información",
                tint = colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                "Selecciona un punto para ver su detalle.",
                color = colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
        return
    }

    // Contenido del panel de detalles
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TitleRow(selectedPoi.emoji, selectedPoi.title)

        if (selectedPoi.description.isNotBlank()) {
            DescriptionBody(selectedPoi.description)
        }

        if (!selectedPoi.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = selectedPoi.imageUrl,
                contentDescription = "Imagen de ${selectedPoi.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// Fila para el título y el emoji
@Composable
private fun TitleRow(emoji: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        if (emoji.isNotBlank()) {
            Text(
                text = emoji,
                style = typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface
        )
    }
}

// Cuerpo para la descripción del POI
@Composable
private fun DescriptionBody(description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Text(
            text = description,
            modifier = Modifier.padding(16.dp),
            style = typography.bodyLarge,
            color = colorScheme.onSurface
        )
    }
}
