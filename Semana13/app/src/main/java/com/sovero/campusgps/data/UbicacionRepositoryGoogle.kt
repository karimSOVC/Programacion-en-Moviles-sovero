package com.sovero.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sovero.campusgps.model.Posicion
import kotlinx.coroutines.tasks.await

/**
 * Obtiene la ubicación del celular con FusedLocationProviderClient
 * (Google Play Services).
 *
 * Es el único lugar de la app que habla con el servicio de ubicación.
 */
class UbicacionRepositoryGoogle(context: Context) : UbicacionRepository {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)

    // El permiso se pide en la pantalla antes de llamar a este método.
    // Si falta, Google lanza SecurityException y se devuelve como fallo.
    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> = try {
        val ubicacion = cliente.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()

        if (ubicacion != null) {
            Result.success(Posicion(ubicacion.latitude, ubicacion.longitude))
        } else {
            Result.failure(Exception("No se pudo obtener la ubicación"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
