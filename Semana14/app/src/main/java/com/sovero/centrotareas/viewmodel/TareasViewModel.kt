package com.sovero.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.centrotareas.data.TareasRepository
import com.sovero.centrotareas.model.CargaUi
import com.sovero.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    // ----- Tarea con progreso (pasos 1 a 4)
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // Paso 3: avance de la tarea, de 0f (0 %) a 1f (100 %)
    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    /**
     * Pasos 2 y 3: la tarea se hace en una corrutina, en segundo plano.
     * viewModelScope.launch empieza en el hilo principal; withContext pasa
     * el trabajo a Dispatchers.Default y al terminar vuelve al principal.
     * Son 100 vueltas de 40 ms (≈ 4 s) y en cada una sube el progreso.
     */
    fun tareaLarga() {
        // Paso 4: si ya está corriendo, no se lanza otra
        if (_corriendo.value) return

        trabajo = viewModelScope.launch {
            _corriendo.value = true
            try {
                _progreso.value = 0f
                _mensaje.value = "Trabajando..."
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")
                withContext(Dispatchers.Default) {
                    Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                    for (i in 1..100) {
                        delay(40)
                        _progreso.value = i / 100f
                    }
                }
                Log.d("HILO", "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } finally {
                // Termine bien o la cancelen, deja de estar corriendo
                _corriendo.value = false
            }
        }
    }

    // Paso 4: la corrutina de la tarea, para poder cancelarla
    private var trabajo: Job? = null

    // true mientras la tarea trabaja
    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    /** Detiene la tarea; la barra se queda donde estaba. */
    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    // ----- Contar primos (paso 5)
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    // La corrutina del cálculo, para poder cancelarla
    private var trabajoPrimos: Job? = null

    /**
     * Paso 5: pide al Repository contar los primos hasta [hasta] y mide
     * cuánto tardó. Si lo cancelan, el estado pasa a Cancelado.
     */
    fun contar(hasta: Int) {
        // Si ya está calculando, no se lanza otro cálculo
        if (_primos.value is PrimosUi.Calculando) return

        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repo.contarPrimos(hasta)
                val ms = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, ms)
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                // Se vuelve a lanzar para que la cancelación no se rompa
                throw e
            }
        }
    }

    /** Detiene el cálculo de primos. */
    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }

    // ----- Dos cargas (pasos 6 y 7)
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    /**
     * Paso 6: primero el perfil (2 s) y después los pedidos (3 s).
     * Como una espera a la otra, tarda ≈ 5 s.
     */
    fun cargarUnaPorUna() {
        if (_carga.value is CargaUi.Cargando) return

        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            val perfil = repo.cargarPerfil()
            val pedidos = repo.cargarPedidos(false)
            val ms = System.currentTimeMillis() - inicio
            _carga.value = CargaUi.Exito("Una por una", perfil, pedidos.size, ms)
        }
    }

    /**
     * Paso 6: las dos cargas empiezan al mismo tiempo con async y se
     * espera a las dos con await(). Tarda lo que la más lenta: ≈ 3 s.
     */
    fun cargarALaVez() {
        if (_carga.value is CargaUi.Cargando) return

        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            val (perfil, pedidos) = coroutineScope {
                val a = async { repo.cargarPerfil() }
                val b = async { repo.cargarPedidos(false) }
                Pair(a.await(), b.await())
            }
            val ms = System.currentTimeMillis() - inicio
            _carga.value = CargaUi.Exito("A la vez", perfil, pedidos.size, ms)
        }
    }

    // TODO Paso 7: simularError y manejo de errores
}
