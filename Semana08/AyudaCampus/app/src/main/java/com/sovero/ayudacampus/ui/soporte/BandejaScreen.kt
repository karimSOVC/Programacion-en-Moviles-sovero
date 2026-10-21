package com.sovero.ayudacampus.ui.soporte

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.model.Prioridad
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.components.TicketCard
import com.sovero.ayudacampus.ui.components.iconoCategoria
import com.sovero.ayudacampus.viewmodel.BandejaViewModel

@Composable
fun BandejaScreen(
    vm: BandejaViewModel,
    onAtender: (Int) -> Unit,
    onAtras: () -> Unit
) {
    val tickets by vm.tickets.collectAsState()
    val estado by vm.estadoSeleccionado.collectAsState()
    val prioridad by vm.prioridadSeleccionada.collectAsState()
    val categoriaId by vm.categoriaSeleccionada.collectAsState()
    val texto by vm.textoBusqueda.collectAsState()
    val categorias by vm.categorias.collectAsState()
    val sla by vm.sla.collectAsState()

    // Chips de estado: null significa "Todos"
    val filtrosEstado: List<Pair<String, EstadoTicket?>> = listOf(
        "Todos" to null,
        "Abiertos" to EstadoTicket.ABIERTO,
        "En proceso" to EstadoTicket.EN_PROCESO,
        "Resueltos" to EstadoTicket.RESUELTO
    )

    val hayFiltros = estado != null || prioridad != null || categoriaId != null || texto.isNotBlank()

    Scaffold(
        topBar = { AppTopBar(titulo = "Tickets", onAtras = onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Buscador por titulo, numero o cliente
            OutlinedTextField(
                value = texto,
                onValueChange = { vm.buscar(it) },
                placeholder = { Text("Buscar por título, número o cliente") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // Estado
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 8.dp)
            ) {
                filtrosEstado.forEach { filtro ->
                    FilterChip(
                        selected = estado == filtro.second,
                        onClick = { vm.elegirEstado(filtro.second) },
                        label = { Text(filtro.first) }
                    )
                }
            }

            // Prioridad
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FilterChip(
                    selected = prioridad == null,
                    onClick = { vm.elegirPrioridad(null) },
                    label = { Text("Toda prioridad") }
                )
                Prioridad.entries.reversed().forEach { p ->
                    FilterChip(
                        selected = prioridad == p,
                        onClick = { vm.elegirPrioridad(p) },
                        label = { Text(p.etiqueta) }
                    )
                }
            }

            // Categoria
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                FilterChip(
                    selected = categoriaId == null,
                    onClick = { vm.elegirCategoria(null) },
                    label = { Text("Toda categoría") }
                )
                categorias.forEach { c ->
                    FilterChip(
                        selected = categoriaId == c.id,
                        onClick = { vm.elegirCategoria(c.id) },
                        label = { Text(c.nombre) },
                        leadingIcon = {
                            Icon(
                                imageVector = iconoCategoria(c.icono),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            // Cantidad de resultados y boton para limpiar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${tickets.size} tickets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (hayFiltros) {
                    TextButton(onClick = { vm.limpiarFiltros() }) {
                        Text("Limpiar filtros")
                    }
                }
            }

            if (tickets.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No hay tickets con estos filtros",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(tickets, key = { it.ticket.id }) { t ->
                        TicketCard(
                            numero = t.ticket.id,
                            titulo = t.ticket.titulo,
                            categoria = t.categoria.nombre,
                            icono = t.categoria.icono,
                            estado = EstadoTicket.valueOf(t.ticket.estado),
                            prioridad = Prioridad.valueOf(t.ticket.prioridad),
                            onClick = { onAtender(t.ticket.id) },
                            subtitulo = t.cliente.nombre, // nombre del cliente
                            sla = sla[t.ticket.id]?.texto ?: "",
                            slaVencido = sla[t.ticket.id]?.vencido ?: false
                        )
                    }
                }
            }
        }
    }
}
