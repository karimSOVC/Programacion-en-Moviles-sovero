package com.sovero.ayudacampus.ui.soporte

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.sovero.ayudacampus.viewmodel.BandejaViewModel

@Composable
fun BandejaScreen(
    vm: BandejaViewModel,
    onAtender: (Int) -> Unit,
    onAtras: () -> Unit
) {
    val tickets by vm.tickets.collectAsState()
    val estado by vm.estadoSeleccionado.collectAsState()
    val sla by vm.sla.collectAsState()

    // Chips de filtro: null significa "Todos"
    val filtros: List<Pair<String, EstadoTicket?>> = listOf(
        "Todos" to null,
        "Abiertos" to EstadoTicket.ABIERTO,
        "En proceso" to EstadoTicket.EN_PROCESO,
        "Resueltos" to EstadoTicket.RESUELTO
    )

    Scaffold(
        topBar = { AppTopBar(titulo = "Tickets", onAtras = onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                filtros.forEach { filtro ->
                    FilterChip(
                        selected = estado == filtro.second,
                        onClick = { vm.elegirEstado(filtro.second) },
                        label = { Text(filtro.first) }
                    )
                }
            }

            if (tickets.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No hay tickets aquí",
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
