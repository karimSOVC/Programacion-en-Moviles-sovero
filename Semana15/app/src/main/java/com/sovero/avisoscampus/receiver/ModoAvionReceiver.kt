package com.sovero.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Escucha el anuncio del sistema que avisa cuando el usuario activa o
 * desactiva el modo avión. No decide nada: solo le pasa el dato a
 * [alCambiar] (true = activado).
 */
class ModoAvionReceiver(private val alCambiar: (Boolean) -> Unit) : BroadcastReceiver() {

    // onReceive debe ser corto: lee el dato y lo entrega, nada más
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            alCambiar(intent.getBooleanExtra("state", false))
        }
    }
}
