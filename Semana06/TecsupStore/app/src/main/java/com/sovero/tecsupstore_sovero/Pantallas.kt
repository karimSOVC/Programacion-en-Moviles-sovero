package com.sovero.tecsupstore_sovero

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
fun PantallaFavoritos(
    favoritosIds: List<Int>,
    onToggleFavorito: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val productosFavoritos = productosLista.filter { favoritosIds.contains(it.id) }

    if (productosFavoritos.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Aun no tienes productos favoritos")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productosFavoritos) { producto ->
                TarjetaProducto(
                    producto = producto,
                    esFavorito = true,
                    onToggleFavorito = { onToggleFavorito(producto.id) }
                )
            }
        }
    }
}

@Composable
fun PantallaPerfil(modifier: Modifier = Modifier) {
    PantallaSimple(texto = "Aqui estara tu perfil", modifier = modifier)
}
