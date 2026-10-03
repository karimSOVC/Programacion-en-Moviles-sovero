package com.sovero.tecsupstore_sovero

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var destinoActual by remember { mutableStateOf("Inicio") }
    val favoritosIds = remember { mutableStateListOf<Int>() }

    val toggleFavorito: (Int) -> Unit = { id ->
        if (favoritosIds.contains(id)) {
            favoritosIds.remove(id)
        } else {
            favoritosIds.add(id)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                destinoActual = destinoActual,
                cantidadFavoritos = favoritosIds.size,
                onItemClick = { destino ->
                    if (destino != "Cerrar sesion") {
                        destinoActual = destino
                    }
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (destinoActual == "Inicio") {
                            Column {
                                Text(
                                    text = "TECSUP Store",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "Mas vendidos",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            Text(text = destinoActual)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Abrir menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            when (destinoActual) {
                "Mis pedidos" -> PantallaPedidos(modifier = modifier)
                "Favoritos" -> PantallaFavoritos(
                    favoritosIds = favoritosIds,
                    onToggleFavorito = toggleFavorito,
                    modifier = modifier
                )
                "Perfil" -> PantallaPerfil(modifier = modifier)
                else -> PantallaInicio(
                    favoritosIds = favoritosIds,
                    onToggleFavorito = toggleFavorito,
                    modifier = modifier
                )
            }
        }
    }
}
