package com.sovero.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.avisoscampus.data.AvisosRepository
import com.sovero.avisoscampus.model.Aviso
import com.sovero.avisoscampus.model.Origen
import com.sovero.avisoscampus.model.PermisoUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvisosViewModel(private val repo: AvisosRepository) : ViewModel() {

    val avisos: StateFlow<List<Aviso>> = repo.avisos

    // Evento de una sola vez: "muestra esta notificación".
    // El ViewModel NO tiene Context: lo recibe la pantalla.
    private val _eventos = MutableSharedFlow<Aviso>()
    val eventos: SharedFlow<Aviso> = _eventos.asSharedFlow()

    private val _permiso = MutableStateFlow<PermisoUi>(PermisoUi.SinPedir)
    val permiso: StateFlow<PermisoUi> = _permiso.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /**
     * Paso 2: la pantalla avisa qué respondió el usuario en el cuadro del
     * permiso (o que ya estaba concedido). El ViewModel solo guarda el estado.
     */
    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) PermisoUi.Concedido else PermisoUi.Denegado
    }

    /**
     * Paso 3: anota el aviso en el Repository y emite el evento para que
     * la pantalla lo muestre como notificación.
     */
    fun avisarAhora() {
        viewModelScope.launch {
            val aviso = repo.registrar("Tarea lista", "Tu entrega fue registrada", Origen.BOTON)
            _eventos.emit(aviso)
        }
    }

    // Paso 5: la corrutina del recordatorio, para poder cancelarla
    private var trabajo: Job? = null

    // Segundos que faltan, o null si no hay recordatorio activo
    private val _faltan = MutableStateFlow<Int?>(null)
    val faltan: StateFlow<Int?> = _faltan.asStateFlow()

    /**
     * Paso 5: cuenta hacia atrás y, al llegar a 0, registra el aviso
     * Recordatorio y lo emite. Como corre en viewModelScope, sigue aunque
     * se minimice la app o se gire la pantalla.
     */
    fun recordarEn(segundos: Int = 10) {
        // Si ya hay uno activo, no se lanza otro
        if (trabajo?.isActive == true) return

        trabajo = viewModelScope.launch {
            try {
                for (s in segundos downTo 1) {
                    _faltan.value = s
                    delay(1000)
                }
                val aviso = repo.registrar(
                    "Recordatorio",
                    "Revisa tu laboratorio pendiente",
                    Origen.RECORDATORIO
                )
                _eventos.emit(aviso)
            } finally {
                // Termine bien o lo cancelen, ya no hay recordatorio activo
                _faltan.value = null
            }
        }
    }

    /** Detiene la cuenta: no se emite ningún aviso. */
    fun cancelarRecordatorio() {
        trabajo?.cancel()
    }
}
