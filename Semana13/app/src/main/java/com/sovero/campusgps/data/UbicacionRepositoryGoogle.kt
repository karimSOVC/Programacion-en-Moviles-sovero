package com.sovero.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sovero.campusgps.model.Posicion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Obtiene la ubicación del celular con FusedLocationProviderClient
 * (Google Play Services).
 *
 * Es el único lugar de la app que habla con el servicio de ubicación.
 */
class UbicacionRepositoryGoogle(private val context: Context) : UbicacionRepository {

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
            // null = no se pudo determinar (por ejemplo, GPS apagado)
            Result.failure(UbicacionNoDisponibleException())
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun playServicesDisponible(): Boolean =
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

    /**
     * Mejora (Fase 2): requestLocationUpdates trabaja con un callback
     * (Google llama a onLocationResult cada vez que hay posición nueva).
     * callbackFlow convierte ese callback en un Flow que el ViewModel
     * puede recolectar con collect.
     */
    @SuppressLint("MissingPermission")
    override fun seguirPosicion(): Flow<Posicion> = callbackFlow {
        // Pide una posición cada 3 s, solo si te moviste al menos 2 m
        val pedido = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateDistanceMeters(2f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(resultado: LocationResult) {
                val ubicacion = resultado.lastLocation
                if (ubicacion != null) {
                    // Manda la posición nueva al Flow
                    trySend(Posicion(ubicacion.latitude, ubicacion.longitude))
                }
            }
        }

        try {
            cliente.requestLocationUpdates(pedido, callback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            // Sin permiso: el Flow termina con el error
            close(e)
        }

        // Cuando el ViewModel deja de escuchar (Detener), se apaga el GPS
        awaitClose { cliente.removeLocationUpdates(callback) }
    }
}
