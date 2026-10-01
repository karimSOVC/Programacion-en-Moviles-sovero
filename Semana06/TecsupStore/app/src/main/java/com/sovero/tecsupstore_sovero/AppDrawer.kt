package com.sovero.tecsupstore_sovero

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(onItemClick: (String) -> Unit) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))
        NavigationDrawerItem(
            label = { Text("Inicio") },
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = null) },
            selected = false,
            onClick = { onItemClick("Inicio") }
        )
        NavigationDrawerItem(
            label = { Text("Mis pedidos") },
            icon = { Icon(imageVector = Icons.Filled.ShoppingBag, contentDescription = null) },
            selected = false,
            onClick = { onItemClick("Mis pedidos") }
        )
        NavigationDrawerItem(
            label = { Text("Favoritos") },
            icon = { Icon(imageVector = Icons.Filled.Favorite, contentDescription = null) },
            selected = false,
            onClick = { onItemClick("Favoritos") }
        )
        NavigationDrawerItem(
            label = { Text("Perfil") },
            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = null) },
            selected = false,
            onClick = { onItemClick("Perfil") }
        )
    }
}