package com.tecsup.tecsupfit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PantallaReservas() {
    Text(text = "Aquí se mostrarán tus reservas", modifier = Modifier.padding(16.dp))
}

@Composable
fun PantallaRutinas() {
    Text(text = "Aquí se mostrarán tus rutinas", modifier = Modifier.padding(16.dp))
}

@Composable
fun PantallaPerfil() {
    Text(text = "Aquí se mostrará tu perfil", modifier = Modifier.padding(16.dp))
}
