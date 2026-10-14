package com.sovero.ayudacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// Un dato para los graficos: etiqueta, valor y color
data class DatoGrafico(
    val etiqueta: String,
    val valor: Float,
    val color: Color
)

@Composable
fun GraficoDona(datos: List<DatoGrafico>, modifier: Modifier = Modifier) {
    val total = datos.sumOf { it.valor.toDouble() }.toFloat()

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val grosor = 28.dp.toPx()
            val esquina = Offset(grosor / 2, grosor / 2)
            val tamano = Size(size.width - grosor, size.height - grosor)

            if (total == 0f) {
                drawArc(Color.LightGray, 0f, 360f, false, esquina, tamano, style = Stroke(grosor))
            } else {
                var inicio = -90f
                datos.forEach { d ->
                    val barrido = d.valor / total * 360f
                    drawArc(d.color, inicio, barrido, false, esquina, tamano, style = Stroke(grosor))
                    inicio += barrido
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            datos.forEach { d ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(d.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = d.etiqueta + ": " + d.valor.toInt(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
