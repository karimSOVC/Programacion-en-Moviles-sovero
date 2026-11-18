package com.sovero.campusgps.data

import com.sovero.campusgps.model.Posicion
import kotlinx.coroutines.flow.Flow

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>

    // true si el celular tiene Google Play Services (necesario para la ubicación)
    fun playServicesDisponible(): Boolean

    /**
     * Mejora (Fase 2): seguimiento en vivo. Emite una posición nueva cada
     * vez que el celular se mueve, hasta que se deje de escuchar el Flow.
     */
    fun seguirPosicion(): Flow<Posicion>
}

/**
 * Error cuando no se puede saber la ubicación: por ejemplo, con el GPS
 * apagado getCurrentLocation devuelve null.
 */
class UbicacionNoDisponibleException : Exception("No se pudo obtener la ubicación")
