package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.local.entity.Categoria
import com.sovero.ayudacampus.data.local.relation.TicketConDetalle
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class BandejaViewModel(private val repo: AyudaRepository) : ViewModel() {

    // Filtros (null = "Todos" / "Todas")
    private val _estadoSeleccionado = MutableStateFlow<EstadoTicket?>(null)
    val estadoSeleccionado: StateFlow<EstadoTicket?> = _estadoSeleccionado

    private val _prioridadSeleccionada = MutableStateFlow<Prioridad?>(null)
    val prioridadSeleccionada: StateFlow<Prioridad?> = _prioridadSeleccionada

    private val _categoriaSeleccionada = MutableStateFlow<Int?>(null)
    val categoriaSeleccionada: StateFlow<Int?> = _categoriaSeleccionada

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    // Categorias para los chips del filtro
    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    // Busqueda avanzada: estado, prioridad, categoria y texto al mismo tiempo.
    // El DAO ya entrega ALTA, MEDIA, BAJA y el mas reciente primero; filter conserva ese orden
    val tickets: StateFlow<List<TicketConDetalle>> =
        combine(
            todos,
            _estadoSeleccionado,
            _prioridadSeleccionada,
            _categoriaSeleccionada,
            _textoBusqueda
        ) { lista, estado, prioridad, categoriaId, texto ->
            val busqueda = texto.trim().removePrefix("#")
            lista.filter { t ->
                val cumpleEstado = estado == null || t.ticket.estado == estado.name
                val cumplePrioridad = prioridad == null || t.ticket.prioridad == prioridad.name
                val cumpleCategoria = categoriaId == null || t.ticket.categoriaId == categoriaId
                val cumpleTexto = busqueda.isEmpty() ||
                        t.ticket.titulo.contains(busqueda, ignoreCase = true) ||
                        t.cliente.nombre.contains(busqueda, ignoreCase = true) ||
                        t.ticket.id.toString() == busqueda
                cumpleEstado && cumplePrioridad && cumpleCategoria && cumpleTexto
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirEstado(estado: EstadoTicket?) {
        _estadoSeleccionado.value = estado
    }

    fun elegirPrioridad(prioridad: Prioridad?) {
        _prioridadSeleccionada.value = prioridad
    }

    fun elegirCategoria(categoriaId: Int?) {
        _categoriaSeleccionada.value = categoriaId
    }

    fun buscar(texto: String) {
        _textoBusqueda.value = texto
    }

    fun limpiarFiltros() {
        _estadoSeleccionado.value = null
        _prioridadSeleccionada.value = null
        _categoriaSeleccionada.value = null
        _textoBusqueda.value = ""
    }
}
