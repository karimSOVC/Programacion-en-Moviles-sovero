package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.local.relation.TicketConDetalle
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.EstadoTicket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TicketDetalleViewModel(private val repo: AyudaRepository) : ViewModel() {

    private val ticketId = MutableStateFlow(0)

    // Flow: si Soporte cambia el estado o responde, la pantalla se actualiza sola
    val ticket: StateFlow<TicketConDetalle?> = ticketId
        .flatMapLatest { id -> repo.observarTicket(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun cargar(id: Int) {
        ticketId.value = id
    }

    // No hace nada si el texto esta vacio o el ticket esta CERRADO
    fun comentar(texto: String) {
        val t = ticket.value ?: return
        if (texto.isBlank()) return
        if (t.ticket.estado == EstadoTicket.CERRADO.name) return

        viewModelScope.launch {
            // El autor es el estudiante dueño del ticket
            repo.agregarComentario(t.ticket.id, t.cliente, texto.trim())
        }
    }

    // Solo si esta RESUELTO y la calificacion va de 1 a 5
    fun cerrar(calificacion: Int, comentarioFinal: String = "") {
        val t = ticket.value ?: return
        if (t.ticket.estado != EstadoTicket.RESUELTO.name) return
        if (calificacion < 1 || calificacion > 5) return

        viewModelScope.launch {
            if (comentarioFinal.isNotBlank()) {
                repo.agregarComentario(t.ticket.id, t.cliente, comentarioFinal.trim())
            }
            repo.cerrarConCalificacion(t.ticket.id, calificacion)
        }
    }

    // Solo si esta RESUELTO: vuelve a ABIERTO y se borra la calificacion
    fun reabrir() {
        val t = ticket.value ?: return
        if (t.ticket.estado != EstadoTicket.RESUELTO.name) return

        viewModelScope.launch {
            repo.reabrir(t.ticket.id)
        }
    }
}
