package com.tecsup.tecsupfit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.tecsupfit.ui.theme.TecsupFitTheme

@Composable
fun PantallaReservas(reservas: List<Reserva>) {
    if (reservas.isEmpty()) {
        Text(text = "Aún no tienes reservas", modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reservas) { reserva ->
                TarjetaReserva(reserva = reserva)
            }
        }
    }
}

@Composable
fun TarjetaReserva(reserva: Reserva) {
    val esConfirmada = reserva.estado == "Confirmada"
    val colorEstado = if (esConfirmada) Color(0xFF2E7D32) else Color.Gray

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reserva.clase.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = "${reserva.clase.horario}, ${reserva.hora}")
            Text(
                text = reserva.estado,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(colorEstado, MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun PantallaRutinas() {
    Text(text = "Aquí se mostrarán tus rutinas", modifier = Modifier.padding(16.dp))
}

@Composable
fun PantallaPerfil() {
    val nombreUsuario = "Karim Sovero"
    val plan = "Plan Premium"
    val clasesTomadas = 14
    val racha = 3

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = nombreUsuario,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(text = plan)

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaEstadistica(
                valor = clasesTomadas.toString(),
                etiqueta = "Clases tomadas",
                modifier = Modifier.weight(1f)
            )
            TarjetaEstadistica(
                valor = racha.toString(),
                etiqueta = "Racha de asistencia",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TarjetaEstadistica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = etiqueta)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaPerfilPreview() {
    TecsupFitTheme {
        PantallaPerfil()
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaReservasPreview() {
    TecsupFitTheme {
        PantallaReservas(
            reservas = listOf(
                Reserva(listaClases[0], "8:00 am", "Confirmada"),
                Reserva(listaClases[1], "9:30 am", "Completada")
            )
        )
    }
}
