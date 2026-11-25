package com.sovero.avisoscampus.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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

    fun mostrar(context: Context, aviso: Aviso): Boolean {
        return true
    }
}
