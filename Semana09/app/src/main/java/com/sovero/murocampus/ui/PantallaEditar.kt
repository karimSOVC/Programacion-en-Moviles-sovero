package com.sovero.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sovero.murocampus.viewmodel.PostsViewModel
import com.sovero.murocampus.viewmodel.UiState

private val VerdeFondo = Color(0xFFE3F3E4)
private val VerdeTexto = Color(0xFF1B5E20)

/**
 * Mejora (Fase 2): pantalla para cambiar el título de una publicación con
 * PATCH. Debajo muestra lo que respondió el servidor.
 */
@Composable
fun PantallaEditar(
    vm: PostsViewModel,
    id: Int,
    tituloActual: String,
    modifier: Modifier = Modifier
) {
    val estado by vm.edicion.collectAsStateWithLifecycle()
    var titulo by rememberSaveable { mutableStateOf(tituloActual) }
    val enviando = estado is UiState.Cargando

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Editar título de la publicación $id",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        // Solo se puede guardar si el título no está vacío y cambió
        Button(
            onClick = { vm.editarTitulo(id, titulo.trim()) },
            enabled = titulo.isNotBlank() && titulo.trim() != tituloActual && !enviando
        ) {
            Text("Guardar")
        }

        when (val e = estado) {
            null -> {}
            UiState.Cargando -> CircularProgressIndicator()
            is UiState.Exito -> Surface(
                color = VerdeFondo,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, VerdeTexto),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Respuesta del servidor", color = VerdeTexto, fontWeight = FontWeight.Bold)
                    Text("id ${e.datos.id} · Usuario ${e.datos.userId}", color = VerdeTexto)
                    Text(
                        text = "Título: ${e.datos.title}",
                        color = VerdeTexto,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is UiState.Error -> Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = e.mensaje,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
