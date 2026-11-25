package com.sovero.avisoscampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Azul40,
    onPrimary = Color.White,
    primaryContainer = AzulContenedor,
    secondary = Gris40,
    background = Fondo,
    surface = Color.White
)

/**
 * Tema de AvisosCampus: siempre claro y con el azul de la app.
 * No usa los colores del fondo de pantalla del celular, así la barra
 * superior siempre sale azul como en el diseño de la guía.
 */
@Composable
fun AvisosCampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
