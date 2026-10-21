package com.sovero.ayudacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun GraficoBarras(datos: List<DatoGrafico>, modifier: Modifier = Modifier) {
    val maximo = datos.maxOfOrNull { it.valor } ?: 0f

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            if (datos.isNotEmpty()) {
                val espacio = size.width / datos.size
                val anchoBarra = espacio * 0.6f
                datos.forEachIndexed { i, d ->
                    val alto = if (maximo == 0f) 0f else d.valor / maximo * size.height
                    drawRoundRect(
                        color = d.color,
                        topLeft = Offset(i * espacio + (espacio - anchoBarra) / 2, size.height - alto),
                        size = Size(anchoBarra, alto),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            datos.forEach { d ->
                Text(
                    text = d.etiqueta + "\n" + d.valor.toInt(),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
