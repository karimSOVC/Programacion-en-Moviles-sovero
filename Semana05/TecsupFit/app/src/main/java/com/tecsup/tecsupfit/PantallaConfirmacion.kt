package com.tecsup.tecsupfit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.tecsupfit.ui.theme.TecsupFitTheme

@Composable
fun PantallaConfirmacion(
    claseId: Int,
    horarioIndex: Int,
    onVerReservas: () -> Unit = {}
) {
    val clase = listaClases.find { it.id == claseId }
    val hora = clase?.horarioDisponibles?.getOrNull(horarioIndex)

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "¡Reserva confirmada!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (clase == null || hora == null) {
            Text(text = "No se encontraron los datos de la reserva")
        } else {
            Text(text = "Clase: ${clase.nombre}")
            Text(text = "Días: ${clase.horario}")
            Text(text = "Horario: $hora")
        }

        Button(
            onClick = onVerReservas,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ver reservas")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaConfirmacionPreview() {
    TecsupFitTheme {
        PantallaConfirmacion(claseId = 1, horarioIndex = 0)
    }
}
