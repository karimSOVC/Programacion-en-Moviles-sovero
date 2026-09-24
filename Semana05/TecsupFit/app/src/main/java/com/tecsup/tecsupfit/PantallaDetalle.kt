package com.tecsup.tecsupfit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.tecsupfit.ui.theme.TecsupFitTheme

@Composable
fun PantallaDetalle(
    claseId: Int,
    onReservar: (horarioIndex: Int) -> Unit = {}
) {
    val clase = listaClases.find { it.id == claseId }

    var horarioSeleccionado by remember { mutableStateOf(-1) }

    if (clase == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Clase no encontrada")
        }
    } else {
        val horaActual = if (horarioSeleccionado != -1) {
            clase.horarioDisponibles[horarioSeleccionado]
        } else {
            clase.horarioDisponibles.first()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.FitnessCenter,
                        contentDescription = null,
                        tint = Color(0xFF146B55),
                        modifier = Modifier.size(64.dp)
                    )
                }

                Text(
                    text = clase.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$horaActual · ${clase.sala} · ${clase.duracion}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = clase.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Text(
                    text = "${clase.cuposDisponibles} de ${clase.cuposTotales} cupos disponibles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Elige un horario:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(clase.horarioDisponibles) { index, hora ->
                            FilterChip(
                                selected = index == horarioSeleccionado,
                                onClick = { horarioSeleccionado = index },
                                label = { Text(hora) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF146B55),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFFF0F0F0),
                                    labelColor = Color.DarkGray
                                ),
                                border = null
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { onReservar(horarioSeleccionado) },
                enabled = horarioSeleccionado != -1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF146B55),
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Reservar cupo",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
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
