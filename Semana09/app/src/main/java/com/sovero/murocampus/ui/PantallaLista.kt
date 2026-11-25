package com.sovero.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sovero.murocampus.viewmodel.PostsViewModel
import com.sovero.murocampus.viewmodel.UiState

/**
 * Lista de publicaciones. Arriba, los chips del filtro; debajo, el estado
 * del ViewModel con un when: Cargando, Error (con Reintentar) o Exito.
 */
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        // Paso 8: Todos y Usuario 1 a Usuario 10 (se desplaza de lado)
        FiltroUsuarios(
            seleccionado = filtro,
            onElegir = { vm.filtrar(it) }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (val e = estado) {
                UiState.Cargando -> Cargando(modifier = Modifier.align(Alignment.Center))

                is UiState.Error -> ErrorConReintento(
                    mensaje = e.mensaje,
                    onReintentar = { vm.cargar() },
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                is UiState.Exito -> if (e.datos.isEmpty()) {
                    Text(
                        text = "Sin publicaciones",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(e.datos, key = { it.id }) { post ->
                            TarjetaPost(
                                userId = post.userId,
                                titulo = post.title,
                                texto = post.body,
                                maxLineasTexto = 2,
                                onClick = { onAbrir(post.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Fila de chips: el elegido se ve relleno de azul. */
@Composable
private fun FiltroUsuarios(seleccionado: Int?, onElegir: (Int?) -> Unit) {
    // null = Todos; 1..10 = cada usuario
    val opciones: List<Int?> = listOf(null) + (1..10).toList()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { userId ->
            FilterChip(
                selected = seleccionado == userId,
                onClick = { onElegir(userId) },
                label = { Text(if (userId == null) "Todos" else "Usuario $userId") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.padding(0.dp)
            )
        }
    }
}
