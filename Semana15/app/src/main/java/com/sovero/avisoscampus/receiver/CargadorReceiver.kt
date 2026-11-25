package com.sovero.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Mejora (Fase 2): escucha los anuncios del sistema cuando se conecta o se
 * desconecta el cargador. No decide nada: solo le pasa el dato a
 * [alCambiar] (true = conectado).
 */
class CargadorReceiver(private val alCambiar: (Boolean) -> Unit) : BroadcastReceiver() {

    // onReceive debe ser corto: lee el anuncio y lo entrega, nada más
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> alCambiar(true)
            Intent.ACTION_POWER_DISCONNECTED -> alCambiar(false)
        }
    }
}
