package com.sovero.tecsupstore_sovero

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
private fun PantallaSimple(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = texto)
    }
}

@Composable
fun PantallaPedidos(modifier: Modifier = Modifier) {
    PantallaSimple(texto = "Aqui estaran tus pedidos", modifier = modifier)
}

@Composable
fun PantallaFavoritos(modifier: Modifier = Modifier) {
    PantallaSimple(texto = "Aqui estaran tus favoritos", modifier = modifier)
}

@Composable
fun PantallaPerfil(modifier: Modifier = Modifier) {
    PantallaSimple(texto = "Aqui estara tu perfil", modifier = modifier)
}