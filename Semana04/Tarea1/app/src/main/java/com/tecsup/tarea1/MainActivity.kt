package com.tecsup.tarea1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.tarea1.ui.theme.Tarea1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Tarea1Theme {
                TemperatureDisplay()
            }
        }
    }
}

@Composable
fun TemperatureDisplay() {
    var temperatura by remember { mutableStateOf(20) }

    val colorTexto = when {
        temperatura > 30 -> Color.Red
        temperatura < 10 -> Color.Blue
        else -> Color.Black
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Temperatura: $temperatura°",
            style = MaterialTheme.typography.headlineMedium,
            color = colorTexto
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { temperatura++ }) {
                Text("Subir")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { temperatura-- }) {
                Text("Bajar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { temperatura = 20 }) {
            Text("Resetear")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TemperatureDisplayPreview() {
    Tarea1Theme {
        TemperatureDisplay()
    }
}