package com.sovero.avisoscampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sovero.avisoscampus.model.Aviso
import com.sovero.avisoscampus.model.Origen
import com.sovero.avisoscampus.ui.theme.Tarjeta as FondoFila

/**
 * Tarjeta Historial: los 20 avisos más recientes (lo más nuevo arriba).
 * No conoce el ViewModel: solo dibuja la lista que recibe.
 */
@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    Tarjeta("Historial") {
        if (avisos.isEmpty()) {
            Text(
                text = "Sin avisos todavía",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Column con forEach (no LazyColumn): la pantalla ya se desplaza
            // y una lista desplazable dentro de otra da error
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                avisos.take(20).forEach { aviso ->
                    FilaAviso(aviso)
                }
            }
        }
    }
}

/** Una fila: etiqueta del origen, título, texto y hora. */
@Composable
private fun FilaAviso(aviso: Aviso) {
    Surface(
        color = FondoFila,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EtiquetaOrigen(aviso.origen)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(aviso.titulo, fontWeight = FontWeight.Bold)
                Text(
                    text = aviso.texto,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = aviso.hora,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(64.dp)
            )
        }
    }
}

/** Etiqueta de color con el origen: Botón, Recordatorio o Modo avión. */
@Composable
private fun EtiquetaOrigen(origen: Origen) {
    val color = when (origen) {
        Origen.BOTON -> Color(0xFF1E88E5)
        Origen.RECORDATORIO -> Color(0xFFF9A825)
        Origen.MODO_AVION -> Color(0xFF7E57C2)
    }
    Surface(color = color, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = origen.texto,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
