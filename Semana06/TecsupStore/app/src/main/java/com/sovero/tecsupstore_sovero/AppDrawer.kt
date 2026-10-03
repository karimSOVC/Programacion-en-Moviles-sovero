package com.sovero.tecsupstore_sovero

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    destinoActual: String,
    cantidadFavoritos: Int,
    onItemClick: (String) -> Unit
) {
    val destinos = listOf(
        "Inicio" to Icons.Filled.Home,
        "Mis pedidos" to Icons.Filled.ShoppingBag,
        "Favoritos" to Icons.Filled.Favorite,
        "Perfil" to Icons.Filled.Person
    )

    ModalDrawerSheet {
        EncabezadoDrawer()
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        destinos.forEach { (nombre, icono) ->
            NavigationDrawerItem(
                label = { Text(nombre) },
                icon = { Icon(imageVector = icono, contentDescription = null) },
                selected = destinoActual == nombre,
                onClick = { onItemClick(nombre) },
                badge = if (nombre == "Favoritos" && cantidadFavoritos > 0) {
                    { Text(cantidadFavoritos.toString()) }
                } else null,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

@Composable
private fun EncabezadoDrawer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "KS",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = "Karim Sovero", style = MaterialTheme.typography.titleMedium)
            Text(text = "karim@tecsup.edu.pe", style = MaterialTheme.typography.bodySmall)
        }
    }
}
