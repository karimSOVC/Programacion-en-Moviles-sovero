package com.tecsup.tecsupfit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val containerColorEstado = if (esConfirmada) Color(0xFFE8F5E9) else Color(0xFFE0E0E0)
    val textColorEstado = if (esConfirmada) Color(0xFF146B55) else Color.Gray

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (esConfirmada) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(90.dp)
                        .background(Color(0xFF146B55))
                )
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = reserva.clase.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${reserva.clase.horario}, ${reserva.hora}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = reserva.estado,
                    color = textColorEstado,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(containerColorEstado, RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
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

    val iniciales = nombreUsuario.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFFE8F5E9),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF146B55)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = nombreUsuario,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = plan,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
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
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
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
