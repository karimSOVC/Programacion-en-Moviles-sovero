package com.sovero.avisoscampus.notificacion

import android.content.Context
import com.sovero.avisoscampus.model.Aviso

/**
 * Todo lo que toca el sistema de notificaciones de Android: el canal,
 * el permiso y mostrar el aviso. Lo usa la pantalla, nunca el ViewModel.
 */
object Notificador {
    const val CANAL = "avisos"

    fun crearCanal(context: Context) {
    }

    fun mostrar(context: Context, aviso: Aviso): Boolean {
        return true
    }
}
