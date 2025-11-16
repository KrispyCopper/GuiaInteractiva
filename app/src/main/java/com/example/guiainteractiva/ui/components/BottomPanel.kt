package com.example.guiainteractiva.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.guiainteractiva.model.Poi
import com.example.guiainteractiva.ui.theme.*

@Composable
fun AdminBottomPanel(
    modifier: Modifier = Modifier,
    selectedPoi: Poi?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (selectedPoi != null) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderRow(onConfirm, onCancel)

            TitleTextField(
                title = selectedPoi.title,
                onTitleChange = onTitleChange
            )

            BodyTextField(
                description = selectedPoi.description,
                onDescriptionChange = onDescriptionChange
            )

            ImageUploadRow()
        }
    }
}

@Composable
private fun HeaderRow(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Etiqueta "Vista Administrador"
        Surface(
            shape = CircleShape,
            color = PastelGreenBg
        ) {
            Text(
                text = "Vista Administrador",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White, // siempre blanco, simple y consistente
                fontWeight = FontWeight.Bold
            )
        }

        // Botones confirmar / cancelar
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            IconButton(
                onClick = onConfirm,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = ConfirmGreen,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = "Confirmar")
            }

            IconButton(
                onClick = onCancel,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = ErrorRed,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar")
            }
        }
    }
}

@Composable
private fun TitleTextField(title: String, onTitleChange: (String) -> Unit) {

    val fieldColor = if (isSystemInDarkTheme()) DarkGrayField else GrayField

    //   AGREGAR EMOJI INPUT
    //  Aquí va el pequeño "botón/TextField" de 1 caracter
    //  que permitirá agregar un emoji asociado al POI.
    //
    //  Recomendación:
    //  - Envolver tod0 este bloque en una Row {}
    //  - A la izquierda colocar un TextField o Box
    //    que acepte solo 1 carácter (emoji)
    //  - Mantener el TitleTextField actual a la derecha
    //
    //  Ejemplo de estructura:
    //
    //     Row {
    //         EmojiField(...)
    //         Spacer(...)
    //         TextField actual (...)
    //     }
    //  Solo tienes que agregar un nuevo input aca.
    //  Si no entiendes puedes preguntarle a la ia.


    TextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Añadir Título") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = null,
                tint = ConfirmGreen
            )
        },
        textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = fieldColor,
            unfocusedContainerColor = fieldColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun BodyTextField(description: String, onDescriptionChange: (String) -> Unit) {

    val fieldColor = if (isSystemInDarkTheme()) DarkGrayField else GrayField

    TextField(
        value = description,
        onValueChange = onDescriptionChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        placeholder = { Text("Añadir texto") },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = fieldColor,
            unfocusedContainerColor = fieldColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun ImageUploadRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ImagePlaceholder(modifier = Modifier.weight(1f))
        ImagePlaceholder(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ImagePlaceholder(modifier: Modifier = Modifier) {

    val fieldColor = if (isSystemInDarkTheme()) DarkGrayField else GrayField

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { /* Abrir selector de imágenes próximamente */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = fieldColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Añadir Imagen",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
