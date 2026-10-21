package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.local.relation.TicketConDetalle
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BandejaViewModel(private val repo: AyudaRepository) : ViewModel() {

    // null = "Todos"
    private val _estadoSeleccionado = MutableStateFlow<EstadoTicket?>(null)
    val estadoSeleccionado: StateFlow<EstadoTicket?> = _estadoSeleccionado

    private val todos = repo.observarTodosLosTickets()

    // SLA de cada ticket (id del ticket -> texto), se recalcula cada minuto
    val sla: StateFlow<Map<Int, InfoSla>> =
        combine(todos, relojPorMinuto()) { lista, ahora ->
            val mapa = mutableMapOf<Int, InfoSla>()
            lista.forEach { t ->
                val info = calcularSla(t.ticket, ahora)
                if (info != null) {
                    mapa[t.ticket.id] = info
                }
            }
            mapa.toMap()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // El DAO ya entrega ALTA, MEDIA, BAJA y el mas reciente primero; filter conserva ese orden
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(todos, _estadoSeleccionado) { lista, estado ->
            if (estado == null) {
                lista
            } else {
                lista.filter { it.ticket.estado == estado.name }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirEstado(estado: EstadoTicket?) {
        _estadoSeleccionado.value = estado
    }
}
