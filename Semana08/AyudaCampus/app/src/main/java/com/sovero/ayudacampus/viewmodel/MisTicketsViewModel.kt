package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.local.relation.TicketConDetalle
import com.sovero.ayudacampus.data.prefs.PreferenciasStore
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class MisTicketsViewModel(
    private val repo: AyudaRepository,
    prefs: PreferenciasStore
) : ViewModel() {

    // null = "Todos"
    private val _estadoSeleccionado = MutableStateFlow<EstadoTicket?>(null)
    val estadoSeleccionado: StateFlow<EstadoTicket?> = _estadoSeleccionado

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    // Tickets del usuario actual (su id viene de la sesion guardada)
    private val misTickets: Flow<List<TicketConDetalle>> = prefs.usuarioId
        .flatMapLatest { id -> repo.observarTicketsCliente(id) }

    // Combina los tres flujos: la lista, el estado elegido y el texto de busqueda
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(misTickets, _estadoSeleccionado, _textoBusqueda) { lista, estado, texto ->
            lista.filter { t ->
                // "Todos" (null) incluye tambien los CERRADOS
                val cumpleEstado = estado == null || t.ticket.estado == estado.name
                val busqueda = texto.trim().removePrefix("#")
                val cumpleTexto = busqueda.isEmpty() ||
                        t.ticket.titulo.contains(busqueda, ignoreCase = true) ||
                        t.ticket.id.toString() == busqueda
                cumpleEstado && cumpleTexto
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Para el saludo: cuantos tickets tiene abiertos
    val cantidadAbiertos: StateFlow<Int> = misTickets
        .map { lista -> lista.count { it.ticket.estado == EstadoTicket.ABIERTO.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // SLA de cada ticket (id del ticket -> texto), se recalcula cada minuto
    val sla: StateFlow<Map<Int, InfoSla>> =
        combine(misTickets, relojPorMinuto()) { lista, ahora ->
            val mapa = mutableMapOf<Int, InfoSla>()
            lista.forEach { t ->
                val info = calcularSla(t.ticket, ahora)
                if (info != null) {
                    mapa[t.ticket.id] = info
                }
            }
            mapa.toMap()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Contadores para el Perfil
    val cantidadResueltos: StateFlow<Int> = misTickets
        .map { lista -> lista.count { it.ticket.estado == EstadoTicket.RESUELTO.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cantidadTotal: StateFlow<Int> = misTickets
        .map { lista -> lista.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Nombre del estudiante (sale del cliente de sus tickets)
    val nombre: StateFlow<String> = misTickets
        .map { lista -> lista.firstOrNull()?.cliente?.nombre ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun elegirEstado(estado: EstadoTicket?) {
        _estadoSeleccionado.value = estado
    }

    fun buscar(texto: String) {
        _textoBusqueda.value = texto
    }
}
