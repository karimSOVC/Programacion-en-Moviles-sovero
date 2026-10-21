package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.local.entity.Usuario
import com.sovero.ayudacampus.data.local.relation.TicketConDetalle
import com.sovero.ayudacampus.data.prefs.PreferenciasStore
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.model.Prioridad
import com.sovero.ayudacampus.model.Rol
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AtenderViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val ticketId = MutableStateFlow(0)

    val ticket: StateFlow<TicketConDetalle?> = ticketId
        .flatMapLatest { id -> repo.observarTicket(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Cambios elegidos en pantalla (null = sin cambios). Se guardan con guardar()
    private val _estadoElegido = MutableStateFlow<EstadoTicket?>(null)
    val estadoElegido: StateFlow<EstadoTicket?> = _estadoElegido

    private val _prioridadElegida = MutableStateFlow<Prioridad?>(null)
    val prioridadElegida: StateFlow<Prioridad?> = _prioridadElegida

    // true cuando ya se guardaron los cambios, para volver a la bandeja
    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado

    fun cargar(id: Int) {
        ticketId.value = id
    }

    private fun estaCerrado(): Boolean {
        return ticket.value?.ticket?.estado == EstadoTicket.CERRADO.name
    }

    // Soporte solo puede elegir ABIERTO, EN_PROCESO o RESUELTO (nunca CERRADO)
    fun cambiarEstado(estado: EstadoTicket) {
        if (estaCerrado()) return
        if (estado == EstadoTicket.CERRADO) return
        _estadoElegido.value = estado
    }

    fun cambiarPrioridad(p: Prioridad) {
        if (estaCerrado()) return
        _prioridadElegida.value = p
    }

    fun responder(texto: String) {
        val t = ticket.value ?: return
        if (texto.isBlank()) return
        if (estaCerrado()) return

        viewModelScope.launch {
            // El autor es el usuario de soporte que inicio sesion
            val autor = Usuario(
                id = prefs.usuarioId.first(),
                nombre = "Soporte TI",
                correo = "",
                clave = "",
                rol = Rol.ADMIN.name
            )
            repo.agregarComentario(t.ticket.id, autor, texto.trim())
        }
    }

    fun guardar() {
        val t = ticket.value ?: return
        if (estaCerrado()) return

        viewModelScope.launch {
            val estado = _estadoElegido.value
            if (estado != null && estado.name != t.ticket.estado) {
                repo.cambiarEstado(t.ticket.id, estado)
            }
            val prioridad = _prioridadElegida.value
            if (prioridad != null && prioridad.name != t.ticket.prioridad) {
                repo.cambiarPrioridad(t.ticket.id, prioridad)
            }
            _guardado.value = true
        }
    }
}
