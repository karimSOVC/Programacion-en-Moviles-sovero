package com.tecsup.tecsupfit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.tecsupfit.ui.theme.TecsupFitTheme

@Composable
fun PantallaDetalle(
    claseId: Int,
    onVolver: () -> Unit = {}
) {
    // Busca en la lista la clase que tiene el id recibido por navegación
    val clase = listaClases.find { it.id == claseId }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (clase == null) {
            Text(text = "Clase no encontrada")
        } else {
            Text(
                text = clase.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Días: ${clase.horario}")
            Text(text = "Periodo: ${clase.periodo}")
            Text(text = "Horarios disponibles: ${clase.horarioDisponibles.joinToString(", ")}")

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Reservar cupo")
            }
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaDetallePreview() {
    TecsupFitTheme {
        PantallaDetalle(claseId = 1)
    }
}
