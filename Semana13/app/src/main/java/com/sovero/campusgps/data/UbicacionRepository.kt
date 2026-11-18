package com.sovero.campusgps.data

import com.sovero.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>

    // true si el celular tiene Google Play Services (necesario para la ubicación)
    fun playServicesDisponible(): Boolean
}

/**
 * Error cuando no se puede saber la ubicación: por ejemplo, con el GPS
 * apagado getCurrentLocation devuelve null.
 */
class UbicacionNoDisponibleException : Exception("No se pudo obtener la ubicación")
