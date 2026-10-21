package com.sovero.ayudacampus.ui.cliente

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.model.Prioridad
import com.sovero.ayudacampus.model.Rol
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.components.AyudaButton
import com.sovero.ayudacampus.ui.components.AyudaTextField
import com.sovero.ayudacampus.ui.components.BurbujaComentario
import com.sovero.ayudacampus.ui.components.EstadoChip
import com.sovero.ayudacampus.ui.components.Estrellas
import com.sovero.ayudacampus.ui.components.PrioridadChip
import com.sovero.ayudacampus.ui.components.StepperTicket
import com.sovero.ayudacampus.ui.components.iconoCategoria
import com.sovero.ayudacampus.ui.theme.ColorAlta
import com.sovero.ayudacampus.ui.theme.ColorResuelto
import com.sovero.ayudacampus.viewmodel.TicketDetalleViewModel

@Composable
fun DetalleTicketScreen(vm: TicketDetalleViewModel, onAtras: () -> Unit) {
    val detalle by vm.ticket.collectAsState()
    var textoComentario by remember { mutableStateOf("") }
    var calificacion by remember { mutableIntStateOf(0) }
    var comentarioFinal by remember { mutableStateOf("") }

    val d = detalle
    val titulo = if (d == null) "Ticket" else "Ticket #" + d.ticket.id.toString().padStart(4, '0')

    Scaffold(
        topBar = { AppTopBar(titulo = titulo, onAtras = onAtras) },
        bottomBar = {
            // Campo para escribir: solo mientras el ticket esta abierto o en proceso
            if (d != null && (d.ticket.estado == EstadoTicket.ABIERTO.name || d.ticket.estado == EstadoTicket.EN_PROCESO.name)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(12.dp)
                ) {
                    OutlinedTextField(
                        value = textoComentario,
                        onValueChange = { textoComentario = it },
                        placeholder = { Text("Escribe un comentario...") },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            vm.comentar(textoComentario)
                            textoComentario = ""
                        },
                        enabled = textoComentario.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
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

        val estado = EstadoTicket.valueOf(d.ticket.estado)
        val comentarios = d.comentarios.sortedBy { it.fecha }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Cabecera
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = d.ticket.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            EstadoChip(estado)
                            PrioridadChip(Prioridad.valueOf(d.ticket.prioridad))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = iconoCategoria(d.categoria.icono),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = d.categoria.nombre + " · " + d.ticket.ubicacion + " · " + hace(d.ticket.fecha),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Stepper de 4 estados
            item {
                StepperTicket(estado)
            }

            // Aviso verde cuando Soporte lo marco como resuelto
            if (estado == EstadoTicket.RESUELTO) {
                item {
                    Surface(
                        color = ColorResuelto.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = ColorResuelto)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Soporte marcó este ticket como resuelto. ¿El problema se solucionó?",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Conversacion
            item {
                Text(
                    text = "Conversación",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
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
                // Los mensajes del estudiante son los propios (a la derecha)
                val esPropio = c.autorRol == Rol.CLIENTE.name
                BurbujaComentario(
                    texto = c.texto,
                    autorNombre = if (esPropio) "Tú" else c.autorNombre,
                    fecha = c.fecha,
                    esPropio = esPropio
                )
            }

            // Ticket RESUELTO: calificar, cerrar o reabrir
            if (estado == EstadoTicket.RESUELTO) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Califica la atención",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Estrellas(calificacion = calificacion, onCambio = { calificacion = it })
                            Text(
                                text = "$calificacion de 5 estrellas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item {
                    AyudaTextField(
                        valor = comentarioFinal,
                        onCambio = { comentarioFinal = it },
                        etiqueta = "Comentario final (opcional)",
                        lineas = 2
                    )
                }
                item {
                    // Deshabilitado mientras la calificacion sea 0
                    AyudaButton(
                        texto = "Confirmar y cerrar",
                        onClick = { vm.cerrar(calificacion, comentarioFinal) },
                        habilitado = calificacion > 0
                    )
                }
                item {
                    OutlinedButton(
                        onClick = { vm.reabrir() },
                        border = BorderStroke(1.dp, ColorAlta),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Reabrir ticket", color = ColorAlta, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Ticket CERRADO: muestra la calificacion recibida
            if (estado == EstadoTicket.CERRADO) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Calificación recibida",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Estrellas(calificacion = d.ticket.calificacion ?: 0)
                        }
                    }
                }
            }
        }
    }
}

// Texto corto como "Hace 3 h"
private fun hace(fecha: Long): String {
    val minutos = (System.currentTimeMillis() - fecha) / 60000
    return if (minutos < 60) {
        "Hace $minutos min"
    } else if (minutos < 60 * 24) {
        "Hace " + (minutos / 60) + " h"
    } else {
        "Hace " + (minutos / (60 * 24)) + " d"
    }
}
