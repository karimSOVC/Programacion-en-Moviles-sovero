package com.tecsup.mibodega.ui.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.tecsup.mibodega.ui.cliente.Rutas
import com.tecsup.mibodega.ui.theme.VerdeBodega

// Cada ítem del menú inferior: qué texto muestra, qué ícono y a qué ruta lleva.
private data class ItemBarra(
    val etiqueta: String,
    val icono: ImageVector,
    val ruta: String
)

private val itemsBarra = listOf(
    ItemBarra("Inicio", Icons.Default.Home, Rutas.INICIO),
    ItemBarra("Favoritos", Icons.Default.Favorite, Rutas.FAVORITOS),
    ItemBarra("Pedidos", Icons.Default.Receipt, Rutas.PEDIDOS),
    ItemBarra("Perfil", Icons.Default.Person, Rutas.PERFIL)
)

/**
 * Menú principal de la app (NavigationBar). Se usa como bottomBar en
 * Inicio, Categorías, Pedidos y Perfil.
 *
 * No guarda cuál ítem está seleccionado: lo deduce de la ruta actual,
 * así siempre coincide con la pantalla que se está viendo. Tampoco
 * navega sola: avisa a qué ruta ir con onNavegar.
 */
@Composable
fun BarraInferior(
    rutaActual: String,
    onNavegar: (String) -> Unit
) {
    NavigationBar {
        itemsBarra.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = { onNavegar(item.ruta) },
                icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                label = { Text(item.etiqueta) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VerdeBodega,
                    selectedTextColor = VerdeBodega
                )
            )
        }
    }
}
