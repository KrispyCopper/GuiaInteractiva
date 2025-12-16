package com.example.guiainteractiva.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.guiainteractiva.model.Poi

@Composable
fun AdminBottomPanel(
    modifier: Modifier = Modifier,
    selectedPoi: Poi?,
    isLoadingImage: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEmojiChange: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let(onImageSelected) }
    )

    if (selectedPoi != null) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeaderRow(onConfirm, onCancel)
            TitleTextField(selectedPoi.title, onTitleChange, selectedPoi.emoji, onEmojiChange)
            BodyTextField(selectedPoi.description, onDescriptionChange)
            IconAndColorPicker(
                selectedIconName = selectedPoi.iconName,
                selectedColorHex = selectedPoi.colorHex,
                onIconSelected = onIconSelected,
                onColorSelected = onColorSelected
            )
            ImageSection(
                imageUrl = selectedPoi.imageUrl,
                isLoading = isLoadingImage,
                onSelectClick = { imagePickerLauncher.launch("image/*") }
            )
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
        Surface(
            shape = CircleShape,
            color = colorScheme.primary.copy(alpha = 0.8f)
        ) {
            Text(
                text = "Vista Administrador",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = typography.bodyMedium,
                color = colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onConfirm,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = colorScheme.primary.copy(alpha = 0.8f),
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = "Confirmar")
            }
            IconButton(
                onClick = onCancel,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = colorScheme.error.copy(alpha = 0.8f),
                    contentColor = colorScheme.onError
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar")
            }
        }
    }
}

@Composable
private fun TitleTextField(
    title: String,
    onTitleChange: (String) -> Unit,
    emoji: String,
    onEmojiChange: (String) -> Unit
) {
    val fieldColor = colorScheme.surfaceContainer // <-- CORREGIDO

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = emoji,
            onValueChange = onEmojiChange,
            modifier = Modifier.width(70.dp),
            placeholder = { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { Text("😀") } },
            textStyle = typography.headlineSmall.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldColor,
                unfocusedContainerColor = fieldColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Añadir Título") },
            leadingIcon = { Icon(Icons.Default.AddCircle, contentDescription = null, tint = colorScheme.primary) },
            textStyle = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fieldColor,
                unfocusedContainerColor = fieldColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface
            )
        )
    }
}

@Composable
private fun BodyTextField(description: String, onDescriptionChange: (String) -> Unit) {
    val fieldColor = colorScheme.surfaceContainer // <-- CORREGIDO

    TextField(
        value = description,
        onValueChange = onDescriptionChange,
        modifier = Modifier.fillMaxWidth().height(150.dp),
        placeholder = { Text("Añadir texto") },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = fieldColor,
            unfocusedContainerColor = fieldColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface
        )
    )
}

@Composable
private fun ImageSection(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    isLoading: Boolean,
    onSelectClick: () -> Unit
) {
    val fieldColor = colorScheme.surfaceContainer // <-- CORREGIDO

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(fieldColor)
            .clickable(onClick = onSelectClick, enabled = !isLoading),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen del POI",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Añadir Imagen",
                    modifier = Modifier.size(40.dp),
                    tint = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Añadir Imagen",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}
