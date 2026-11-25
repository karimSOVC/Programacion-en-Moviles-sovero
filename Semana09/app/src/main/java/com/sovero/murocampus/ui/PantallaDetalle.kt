package com.sovero.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sovero.murocampus.model.Comentario
import com.sovero.murocampus.ui.theme.Tarjeta
import com.sovero.murocampus.viewmodel.PostsViewModel
import com.sovero.murocampus.viewmodel.UiState

/**
 * Detalle de una publicación: título y texto completos y, debajo,
 * "Comentarios (N)" con el correo y el texto de cada uno.
 */
@Composable
fun PantallaDetalle(vm: PostsViewModel, id: Int, modifier: Modifier = Modifier) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val borrado by vm.borrado.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val e = estado) {
            UiState.Cargando -> Cargando(
                modifier = Modifier.align(Alignment.Center),
                texto = "Cargando publicación…"
            )

            is UiState.Error -> ErrorConReintento(
                mensaje = e.mensaje,
                onReintentar = { vm.abrir(id) },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            is UiState.Exito -> LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // La publicación completa: título y texto sin recortar
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Tarjeta)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = e.datos.post.title, fontWeight = FontWeight.Bold)
                            Text(
                                text = e.datos.post.body,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                // Mejora (Fase 2): botón Eliminar y el código que respondió el servidor
                item {
                    AccionesDetalle(
                        borrado = borrado,
                        onEliminar = { vm.borrar(id) }
                    )
                }
                item {
                    Text(
                        text = "Comentarios (${e.datos.comentarios.size})",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(e.datos.comentarios, key = { it.id }) { comentario ->
                    TarjetaComentario(comentario)
                }
            }
        }
    }
}

/**
 * Mejora (Fase 2): botón Eliminar. Después muestra lo que respondió el
 * servidor: el código HTTP si salió bien o el mensaje de error.
 */
@Composable
private fun AccionesDetalle(borrado: UiState<Int>?, onEliminar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onEliminar,
            // No se puede borrar dos veces ni mientras se está borrando
            enabled = borrado == null || borrado is UiState.Error,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Eliminar")
        }
        when (borrado) {
            null -> {}
            UiState.Cargando -> CircularProgressIndicator()
            is UiState.Exito -> Text(
                text = "Publicación eliminada (código ${borrado.datos})",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            is UiState.Error -> Text(
                text = borrado.mensaje,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/** Un comentario: el correo en azul y el texto debajo. */
@Composable
private fun TarjetaComentario(comentario: Comentario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Tarjeta)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = comentario.email,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = comentario.body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
