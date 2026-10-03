package com.sovero.tecsupstore_sovero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sovero.tecsupstore_sovero.ui.theme.TecsupStoreSoveroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TecsupStoreSoveroTheme {
                AppNavegacion()
            }
        }
    }
}

@Composable
fun PantallaInicio(
    favoritosIds: List<Int>,
    onToggleFavorito: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(productosLista) { producto ->
            TarjetaProducto(
                producto = producto,
                esFavorito = favoritosIds.contains(producto.id),
                onToggleFavorito = { onToggleFavorito(producto.id) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {
    TecsupStoreSoveroTheme {
        PantallaInicio(
            favoritosIds = emptyList(),
            onToggleFavorito = {}
        )
    }
}
