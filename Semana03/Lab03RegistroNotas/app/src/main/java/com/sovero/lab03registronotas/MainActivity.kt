package com.sovero.lab03registronotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.sovero.lab03registronotas.ui.theme.Lab03RegistroNotasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab03RegistroNotasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaNotas(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaNotas(modifier: Modifier = Modifier) {
    var notaFundamentos by remember { mutableStateOf(0f) }
    var notaPoo by remember { mutableStateOf(0f) }
    var notaMoviles by remember { mutableStateOf(0f) }
    var notaBaseDatos by remember { mutableStateOf(0f) }
    var redondear by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }
    var mostrarResultado by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Registro de Notas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Notas del ciclo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Desliza para asignar cada nota (0 a 20)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilaCurso("Fundamentos de Programación", "20%", notaFundamentos) { notaFundamentos = it }
        Spacer(modifier = Modifier.height(16.dp))
        FilaCurso("Programación Orientada a Objetos", "25%", notaPoo) { notaPoo = it }
        Spacer(modifier = Modifier.height(16.dp))
        FilaCurso("Programación en Móviles", "30%", notaMoviles) { notaMoviles = it }
        Spacer(modifier = Modifier.height(16.dp))
        FilaCurso("Base de Datos", "25%", notaBaseDatos) { notaBaseDatos = it }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Redondear promedio final")
            Switch(
                checked = redondear,
                onCheckedChange = { redondear = it }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = confirmado,
                onCheckedChange = { confirmado = it }
            )
            Text("Confirmo que las notas son correctas")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { mostrarResultado = true },
            enabled = confirmado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CALCULAR PROMEDIO")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (mostrarResultado) {
            val ponderado = notaFundamentos * 0.20 + notaPoo * 0.25 +
                    notaMoviles * 0.30 + notaBaseDatos * 0.25
            val promedioFinal = if (redondear) ponderado.roundToInt().toDouble() else ponderado
            val observacion = when {
                promedioFinal >= 17 -> "EXCELENTE"
                promedioFinal >= 13 -> "APROBADO"
                promedioFinal >= 10 -> "EN RECUPERACIÓN"
                else -> "DESAPROBADO"
            }
            val colorChip = when {
                promedioFinal >= 17 -> Color(0xFF1B5E20)
                promedioFinal >= 13 -> Color(0xFF4CAF50)
                promedioFinal >= 10 -> Color(0xFFFFB300)
                else -> Color(0xFFD32F2F)
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Promedio ponderado: " + String.format("%.2f", ponderado))
                    Text(
                        "Promedio final: " + String.format("%.2f", promedioFinal) +
                                if (redondear) " (redondeado)" else "",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = observacion,
                        color = colorChip,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "✓ Promedio calculado correctamente",
                color = Color(0xFF2E7D32)
            )
        } else {
            Text(
                "Asigna las notas y confirma para calcular",
                color = MaterialTheme.colorScheme.outline
            )

        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Desarrollado por: Karim Sovero",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun FilaCurso(
    nombre: String,
    peso: String,
    nota: Float,
    onNotaChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$nombre ($peso)",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = nota.toInt().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = nota,
            onValueChange = onNotaChange,
            valueRange = 0f..20f,
            steps = 19
        )

    }
}