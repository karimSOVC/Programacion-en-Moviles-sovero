package com.sovero.ayudacampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sovero.ayudacampus.model.Rol

// Entrega un ColorScheme segun el perfil: cian (estudiante) o violeta (soporte)
@Composable
fun AyudaTheme(
    rol: Rol = Rol.CLIENTE,
    oscuro: Boolean = false,
    content: @Composable () -> Unit
) {
    val primario = if (rol == Rol.ADMIN) VioletaPrimario else CianPrimario
    val contenedor = if (rol == Rol.ADMIN) VioletaContenedor else CianContenedor
    val fondo = if (rol == Rol.ADMIN) VioletaFondo else CianFondo

    val esquema = if (oscuro) {
        darkColorScheme(
            primary = primario,
            onPrimary = Color.White,
            primaryContainer = primario.copy(alpha = 0.3f),
            onPrimaryContainer = Color.White
        )
    } else {
        lightColorScheme(
            primary = primario,
            onPrimary = Color.White,
            primaryContainer = contenedor,
            onPrimaryContainer = primario,
            background = fondo,
            surface = Color.White
        )
    }

    MaterialTheme(
        colorScheme = esquema,
        typography = Typography,
        content = content
    )
}
