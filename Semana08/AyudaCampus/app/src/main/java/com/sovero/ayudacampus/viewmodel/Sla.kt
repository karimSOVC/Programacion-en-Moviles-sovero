package com.sovero.ayudacampus.viewmodel

import com.sovero.ayudacampus.data.local.entity.Ticket
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.model.Prioridad
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Lo que la pantalla muestra del SLA de un ticket
data class InfoSla(
    val texto: String,
    val vencido: Boolean
)

// Horas de SLA segun la prioridad: Alta 4 h, Media 24 h, Baja 72 h
fun horasSla(prioridad: Prioridad): Int = when (prioridad) {
    Prioridad.ALTA -> 4
    Prioridad.MEDIA -> 24
    Prioridad.BAJA -> 72
}

// Solo corre el SLA mientras el ticket esta ABIERTO o EN_PROCESO
fun calcularSla(ticket: Ticket, ahora: Long): InfoSla? {
    if (ticket.estado != EstadoTicket.ABIERTO.name && ticket.estado != EstadoTicket.EN_PROCESO.name) {
        return null
    }

    val limite = ticket.fecha + horasSla(Prioridad.valueOf(ticket.prioridad)) * 3600 * 1000L
    val restante = limite - ahora

    return if (restante >= 0) {
        InfoSla("Quedan " + textoTiempo(restante), false)
    } else {
        InfoSla("Vencido hace " + textoTiempo(-restante), true)
    }
}

private fun textoTiempo(milisegundos: Long): String {
    val minutos = milisegundos / 60000
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

// Emite la hora actual cada minuto para que el contador se actualice solo
fun relojPorMinuto(): Flow<Long> = flow {
    while (true) {
        emit(System.currentTimeMillis())
        delay(60_000)
    }
}
