package com.sovero.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.campusgps.data.LugaresRepository
import com.sovero.campusgps.data.UbicacionNoDisponibleException
import com.sovero.campusgps.data.UbicacionRepository
import com.sovero.campusgps.model.Lugar
import com.sovero.campusgps.model.LugarConDistancia
import com.sovero.campusgps.model.Posicion
import com.sovero.campusgps.model.TipoError
import com.sovero.campusgps.model.UbicacionUi
import com.sovero.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    // ---- Mejora (Fase 2): filtrar por categoría ----

    /** Categorías de los lugares, sin repetir (Estudio, Comida, Eventos, Servicios). */
    val categorias: List<String> = lugaresRepo.obtenerLugares().map { it.categoria }.distinct()

    // Categoría elegida en los chips (null = todas)
    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria.asStateFlow()

    /** Lugares que se muestran en el mapa y en la lista, según el filtro. */
    val lugaresVisibles: StateFlow<List<LugarConDistancia>> =
        combine(_lugares, _categoria) { lista, cat ->
            if (cat == null) lista else lista.filter { it.lugar.categoria == cat }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _lugares.value
        )

    // ---- Mejora (Fase 2): radio de cercanía ----

    /** Radios que se pueden elegir, en metros. */
    val radios: List<Int> = listOf(100, 200, 500)

    private val _radio = MutableStateFlow(200)
    val radio: StateFlow<Int> = _radio.asStateFlow()

    /** Ids de los lugares que están dentro del radio elegido. */
    val dentroDelRadio: StateFlow<Set<Int>> =
        combine(_lugares, _radio) { lista, metrosRadio ->
            lista.filter { it.metros != null && it.metros <= metrosRadio }
                .map { it.lugar.id }
                .toSet()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    /** Cambia el radio de cercanía (100, 200 o 500 m). */
    fun cambiarRadio(metros: Int) {
        _radio.value = metros
    }

    /** Elige la categoría del filtro; null muestra todas. */
    fun filtrarCategoria(cat: String?) {
        _categoria.value = cat
        // Si el lugar seleccionado ya no se ve, se quita la selección
        val id = _seleccionado.value
        if (id != null && cat != null &&
            _lugares.value.none { it.lugar.id == id && it.lugar.categoria == cat }
        ) {
            _seleccionado.value = null
        }
    }

    fun buscar() {
        // Antes de buscar, revisa que el celular tenga Google Play Services
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }

        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
                    actualizarDistancias(it)
                }
                .onFailure { _estado.value = errorDe(it) }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
    }

    /**
     * Calcula la distancia desde mi posición a cada lugar, ordena la lista
     * del más cercano al más lejano y la publica en _lugares.
     */
    private fun actualizarDistancias(pos: Posicion) {
        _lugares.value = lugaresRepo.obtenerLugares()
            .map { lugar -> LugarConDistancia(lugar, distanciaMetros(pos, lugar)) }
            .sortedBy { it.metros }
    }

    /** Guarda el lugar elegido en la lista (null = ninguno). */
    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }
    /**
     * El lugar más cercano: el primero de la lista ya ordenada (y filtrada)
     * que tenga distancia calculada. Es null mientras no hay ubicación.
     */
    val masCercano: StateFlow<Lugar?> = lugaresVisibles
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Convierte la excepción en un error con su mensaje y su tipo. */
    private fun errorDe(e: Throwable): UbicacionUi.Error = when (e) {
        is SecurityException -> UbicacionUi.Error(
            "Falta el permiso de ubicación",
            TipoError.SIN_PERMISO
        )
        is UbicacionNoDisponibleException -> UbicacionUi.Error(
            "No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo",
            TipoError.GPS_APAGADO
        )
        else -> UbicacionUi.Error(
            e.message ?: "No se pudo obtener la ubicación",
            TipoError.OTRO
        )
    }
}
