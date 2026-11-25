package com.sovero.avisoscampus.notificacion

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.sovero.avisoscampus.model.Aviso

/**
 * Todo lo que toca el sistema de notificaciones de Android: el canal,
 * el permiso y mostrar el aviso. Lo usa la pantalla, nunca el ViewModel.
 */
object Notificador {
    const val CANAL = "avisos"

    /**
     * Crea el canal "Avisos". Desde Android 8 (API 26, O) toda notificación
     * necesita un canal; en versiones anteriores no existen.
     * Crearlo varias veces no pasa nada: Android lo crea una sola vez.
     */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL,
                "Avisos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    /**
     * true si la app puede mostrar notificaciones. Antes de Android 13
     * (API 33) no hay que pedir nada; desde Android 13 se revisa el permiso.
     */
    fun tienePermiso(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun mostrar(context: Context, aviso: Aviso): Boolean {
        return true
    }
}
