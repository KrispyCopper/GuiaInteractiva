package com.example.guiainteractiva.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define los esquemas de color para cada tema de la aplicación

private val EvergreenLightColorScheme = lightColorScheme(
    background = EvergreenLightBackground,
    surface = EvergreenLightSurface,
    primary = EvergreenLightPrimary,
    secondary = EvergreenLightSecondary,
    tertiary = EvergreenLightTextField,
    onBackground = EvergreenLightOnBackground,
    onSurface = EvergreenLightOnSurface,
    onPrimary = EvergreenLightOnPrimary,
    onSecondary = EvergreenLightOnSecondary,
    onTertiary = EvergreenLightOnTextField
)

private val EvergreenDarkColorScheme = darkColorScheme(
    background = EvergreenDarkBackground,
    surface = EvergreenDarkSurface,
    primary = EvergreenDarkPrimary,
    secondary = EvergreenDarkSecondary,
    onBackground = EvergreenDarkOnBackground,
    onSurface = EvergreenDarkOnSurface,
    onPrimary = EvergreenDarkOnPrimary,
    onSecondary = EvergreenDarkOnSecondary
)

private val DaylightColorScheme = lightColorScheme(
    background = DaylightSkyBlue,
    surface = DaylightWarmBeige,
    primary = DaylightPrimary,
    secondary = DaylightSecondary,
    tertiary = DaylightTextField,
    onBackground = DaylightText,
    onSurface = DaylightText,
    onPrimary = DaylightText,
    onSecondary = DaylightText,
    onTertiary = DaylightText
)

private val SunsetColorScheme = lightColorScheme(
    primary = SunsetPrimary,
    secondary = SunsetSecondary,
    background = SunsetGradientStart,
    surface = SunsetSurface,
    tertiary = SunsetTextField,
    onPrimary = SunsetOnPrimary,
    onSecondary = SunsetOnSecondary,
    onBackground = SunsetOnBackground,
    onSurface = SunsetOnSurface,
    onTertiary = Color.White
)

private val NighttimeColorScheme = darkColorScheme(
    primary = NighttimePrimary,
    secondary = NighttimeSecondary,
    background = NighttimeBackground,
    surface = NighttimeSurface,
    tertiary = NighttimeTextField,
    onPrimary = NighttimeOnPrimary,
    onSecondary = NighttimeOnSecondary,
    onBackground = NighttimeOnBackground,
    onSurface = NighttimeOnSurface
)

// Define la estructura para los colores de gradiente
data class GradientColors(val top: Color, val center: Color, val bottom: Color)

// Proveedor de CompositionLocal para los gradientes
private val LocalGradientColors = staticCompositionLocalOf<GradientColors?> { null }

// Composable principal que aplica el tema a la aplicación
@Composable
fun GuiaInteractivaTheme(
    themeName: String = "EvergreenLight",
    content: @Composable () -> Unit
) {
    // Selecciona la paleta de colores, el gradiente y si es un tema oscuro
    val (colorScheme: ColorScheme, gradientColors: GradientColors?, isDark: Boolean) = when (themeName) {
        "EvergreenDark" -> Triple(EvergreenDarkColorScheme, null, true)
        "Daylight" -> Triple(DaylightColorScheme, GradientColors(DaylightSkyGradientStart, DaylightSkyGradientEnd, DaylightSkyGradientEnd), false)
        "Sunset" -> Triple(SunsetColorScheme, GradientColors(SunsetGradientStart, SunsetGradientCenter, SunsetGradientEnd), false)
        "Nighttime" -> Triple(NighttimeColorScheme, null, true)
        else -> Triple(EvergreenLightColorScheme, null, false) // Tema por defecto
    }

    // Efecto para cambiar el color de la barra de estado del sistema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    // Provee los colores del gradiente al resto de la aplicación
    CompositionLocalProvider(LocalGradientColors provides gradientColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Extensión para acceder fácilmente a los colores del gradiente desde MaterialTheme
val MaterialTheme.gradientColors: GradientColors?
    @Composable
    @ReadOnlyComposable
    get() = LocalGradientColors.current
