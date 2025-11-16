package com.example.guiainteractiva.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// PALETAS DE COLORES
// Se toman los colores definidos en Color.kt y se asignan
// a los roles del sistema Material Design.


// Tema Oscuro
private val DarkColorScheme = darkColorScheme(
    background = DarkGreenBg,
    surface = DarkFormBg,
    surfaceVariant = DarkPastelGreenBg,
    primary = GreenButton,
    secondary = DarkBeigeButton,
    tertiary = DarkGrayField,

    onBackground = WhiteText,
    onSurface = WhiteText,
    onSurfaceVariant = WhiteText,
    onPrimary = WhiteText,
    onSecondary = WhiteText,
)

// Tema Claro
private val LightColorScheme = lightColorScheme(
    background = LightGreenBg,
    surface = WhiteText,
    surfaceVariant = PastelGreenBg,
    primary = GreenButton,
    secondary = LightBeigeButton,
    tertiary = GrayField,

    onBackground = BlackText,
    onSurface = BlackText,
    onSurfaceVariant = BlackText,
    onPrimary = WhiteText,
    onSecondary = BlackText,
)
// TEMA PRINCIPAL DE LA APP
// Detecta modo claro/oscuro y aplica la paleta correcta.
// También ajusta la barra de estado del teléfono.
@Composable
fun GuiaInteractivaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
