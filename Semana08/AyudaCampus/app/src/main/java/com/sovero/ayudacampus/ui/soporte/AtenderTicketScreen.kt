package com.sovero.ayudacampus.ui.soporte

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.model.Prioridad
import com.sovero.ayudacampus.model.Rol
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.components.AyudaButton
import com.sovero.ayudacampus.ui.components.BurbujaComentario
import com.sovero.ayudacampus.ui.theme.colorPrioridad
import com.sovero.ayudacampus.viewmodel.AtenderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtenderTicketScreen(
    vm: AtenderViewModel,
    onAtras: () -> Unit,
    onGuardado: () -> Unit
) {
    val detalle by vm.ticket.collectAsState()
    val estadoElegido by vm.estadoElegido.collectAsState()
    val prioridadElegida by vm.prioridadElegida.collectAsState()
    val guardado by vm.guardado.collectAsState()
    var respuesta by remember { mutableStateOf("") }

    LaunchedEffect(guardado) {
        if (guardado) {
            onGuardado()
        }
    }

    val d = detalle
    val titulo = if (d == null) "Atender ticket" else "Atender ticket #" + d.ticket.id.toString().padStart(4, '0')
    val cerrado = d != null && d.ticket.estado == EstadoTicket.CERRADO.name

    Scaffold(
        topBar = { AppTopBar(titulo = titulo, onAtras = onAtras) },
        bottomBar = {
            if (d != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(12.dp)
                ) {
                    if (cerrado) {
                        Text(
                            text = "Este ticket está cerrado: ya no se puede cambiar ni responder.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Campo para responder con boton enviar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = respuesta,
                                onValueChange = { respuesta = it },
                                placeholder = { Text("Responder al cliente...") },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledIconButton(
                                onClick = {
                                    vm.responder(respuesta)
                                    respuesta = ""
                                },
                                enabled = respuesta.isNotBlank()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AyudaButton(texto = "Guardar cambios", onClick = { vm.guardar() })
                    }
                }
            }
        }
    ) { padding ->
        if (d == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        // Lo que se ve seleccionado: lo elegido o, si no hay cambios, lo guardado
        val estadoActual = estadoElegido ?: EstadoTicket.valueOf(d.ticket.estado)
        val prioridadActual = prioridadElegida ?: Prioridad.valueOf(d.ticket.prioridad)
        val comentarios = d.comentarios.sortedBy { it.fecha }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Datos del ticket y del cliente
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = d.ticket.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = d.cliente.nombre + " · " + d.ticket.ubicacion + " · " + d.categoria.nombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = d.ticket.descripcion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Selector de estado (CERRADO siempre deshabilitado)
            item {
                Column {
                    Text(text = "Estado", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        EstadoTicket.entries.forEach { e ->
                            FilterChip(
                                selected = estadoActual == e,
                                onClick = { vm.cambiarEstado(e) },
                                label = { Text(e.etiqueta) },
                                enabled = !cerrado && e != EstadoTicket.CERRADO
                            )
                        }
                    }
                }
            }

            // Selector de prioridad
            item {
                Column {
                    Text(text = "Prioridad", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        Prioridad.entries.forEachIndexed { i, p ->
                            SegmentedButton(
                                selected = prioridadActual == p,
                                onClick = { vm.cambiarPrioridad(p) },
                                enabled = !cerrado,
                                shape = SegmentedButtonDefaults.itemShape(index = i, count = Prioridad.entries.size),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = colorPrioridad(p),
                                    activeContentColor = Color.White
                                )
                            ) {
                                Text(p.etiqueta)
                            }
                        }
                    }
                }
            }

            // Conversacion: los mensajes de soporte son los propios (a la derecha)
            item {
                Text(text = "Conversación", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            if (comentarios.isEmpty()) {
                item {
                    Text(
                        text = "Todavía no hay comentarios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(comentarios, key = { it.id }) { c ->
                val esPropio = c.autorRol == Rol.ADMIN.name
                BurbujaComentario(
                    texto = c.texto,
                    autorNombre = if (esPropio) "Tú" else c.autorNombre,
                    fecha = c.fecha,
                    esPropio = esPropio
                )
            }
        }
    }
}
