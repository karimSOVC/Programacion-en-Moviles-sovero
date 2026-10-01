package com.sovero.tecsupstore_sovero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaInicio(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(modifier: Modifier = Modifier) {
    val productos = listOf(
        Producto(1, "Laptop Lenovo", "Tecnologia", 2499.0),
        Producto(2, "Mouse inalambrico", "Tecnologia", 59.9),
        Producto(3, "Polo Tecsup", "Ropa", 45.0),
        Producto(4, "Mochila", "Accesorios", 120.0),
        Producto(5, "Cuaderno A4", "Utiles", 12.5)
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(productos) { producto ->
            TarjetaProducto(producto = producto)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {
    TecsupStoreSoveroTheme {
        PantallaInicio()
    }
}