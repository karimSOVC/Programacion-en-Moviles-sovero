package com.sovero.ayudacampus.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Calificacion de 1 a 5. Si onCambio es null, solo se muestra (no se puede tocar)
@Composable
fun Estrellas(
    calificacion: Int,
    onCambio: ((Int) -> Unit)? = null
) {
    Row {
        for (i in 1..5) {
            var modificador = Modifier.size(36.dp)
            if (onCambio != null) {
                modificador = modificador.clickable { onCambio(i) }
            }
            Icon(
                imageVector = if (i <= calificacion) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "Estrella $i",
                tint = Color(0xFFF59E0B),
                modifier = modificador
            )
        }
    }
}
