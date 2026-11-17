package com.sovero.campusgps.util

import android.location.Location
import com.sovero.campusgps.model.Lugar
import com.sovero.campusgps.model.Posicion

/**
 * Distancia en metros desde mi posición hasta un lugar.
 *
 * Location.distanceBetween guarda el resultado en resultado[0].
 * El orden es (lat1, lon1, lat2, lon2, resultado).
 */
fun distanciaMetros(desde: Posicion, hasta: Lugar): Float {
    val resultado = FloatArray(1)
    Location.distanceBetween(
        desde.latitud, desde.longitud,
        hasta.latitud, hasta.longitud,
        resultado
    )
    return resultado[0]
}
