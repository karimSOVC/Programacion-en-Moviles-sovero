package com.sovero.campusgps.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Azul80,
    secondary = Gris80
)

private val LightColorScheme = lightColorScheme(
    primary = Azul40,
    onPrimary = Color.White,
    primaryContainer = AzulContenedor,
    secondary = Gris40,
    // Fondo blanco y tarjetas en gris claro, como en el diseño
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color(0xFFEFF3F8)
)

/**
 * Tema de CampusGPS con el azul de la app.
 * No usa los colores del fondo de pantalla del celular, así la barra
 * superior siempre sale azul como pide la guía. Siempre usa el tema claro
 * (fondo blanco), aunque el celular esté en modo oscuro.
 */
@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
