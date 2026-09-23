package com.tecsup.tecsupfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.tecsupfit.ui.theme.TecsupFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TecsupFitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaInicio(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(modifier: Modifier = Modifier) {
    var filtroSeleccionado by remember { mutableStateOf("Hoy") }
    val filtros = listOf("Hoy", "Esta semana")

    val clasesFiltradas = listaClases.filter { it.periodo == filtroSeleccionado }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Clases disponibles",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Fila de chips de filtro
        LazyRow(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtros) { filtro ->
                FilterChip(
                    selected = filtro == filtroSeleccionado,
                    onClick = { filtroSeleccionado = filtro },
                    label = { Text(filtro) }
                )
            }
        }

        // Lista de clases filtradas
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(clasesFiltradas) { clase ->
                TarjetaClase(clase = clase)
            }
        }
    }
}

@Composable
fun TarjetaClase(clase: ClaseGym) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = clase.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = clase.horario)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {
    TecsupFitTheme {
        PantallaInicio()
    }
}
