package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.ui.components.graficos.DatoGrafico
import com.sovero.ayudacampus.ui.theme.ColorAbierto
import com.sovero.ayudacampus.ui.theme.ColorCerrado
import com.sovero.ayudacampus.ui.theme.ColorEnProceso
import com.sovero.ayudacampus.ui.theme.ColorResuelto
import com.sovero.ayudacampus.ui.theme.VioletaPrimario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PanelViewModel(private val repo: AyudaRepository) : ViewModel() {

    private val flujoAbiertos = repo.observarCantidadPorEstado(EstadoTicket.ABIERTO)
    private val flujoEnProceso = repo.observarCantidadPorEstado(EstadoTicket.EN_PROCESO)
    private val flujoResueltos = repo.observarCantidadPorEstado(EstadoTicket.RESUELTO)
    private val flujoCerrados = repo.observarCantidadPorEstado(EstadoTicket.CERRADO)

    // Contadores
    val abiertos: StateFlow<Int> = flujoAbiertos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val enProceso: StateFlow<Int> = flujoEnProceso
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val resueltos: StateFlow<Int> = flujoResueltos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Datos para el grafico de dona: tickets por estado
    val porEstado: StateFlow<List<DatoGrafico>> =
        combine(flujoAbiertos, flujoEnProceso, flujoResueltos, flujoCerrados) { a, p, r, c ->
            listOf(
                DatoGrafico("Abierto", a.toFloat(), ColorAbierto),
                DatoGrafico("En proceso", p.toFloat(), ColorEnProceso),
                DatoGrafico("Resuelto", r.toFloat(), ColorResuelto),
                DatoGrafico("Cerrado", c.toFloat(), ColorCerrado)
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Datos para el grafico de barras: tickets por categoria
    val porCategoria: StateFlow<List<DatoGrafico>> = repo.observarCantidadPorCategoria()
        .map { lista ->
            lista.map { conteo -> DatoGrafico(conteo.nombre, conteo.cantidad.toFloat(), VioletaPrimario) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tickets ALTA abiertos con mas de 1 hora (limite = ahora - 1 hora)
    val altasSinAtender: StateFlow<Int> = repo
        .observarAltasSinAtender(System.currentTimeMillis() - 3600 * 1000L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Tiempo promedio de resolucion (fechaCierre - fecha) ya convertido a texto
    val tiempoPromedio: StateFlow<String> = repo.observarTiempoPromedioResolucion()
        .map { promedio -> textoDuracion(promedio) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sin tickets cerrados")

    private fun textoDuracion(milisegundos: Double?): String {
        if (milisegundos == null) return "Sin tickets cerrados"

        val minutos = (milisegundos / 60000).toLong()
        val horas = minutos / 60
        val dias = horas / 24

        return if (minutos < 60) {
            "$minutos min"
        } else if (horas < 24) {
            "$horas h " + (minutos % 60) + " min"
        } else {
            "$dias d " + (horas % 24) + " h"
        }
    }
}
