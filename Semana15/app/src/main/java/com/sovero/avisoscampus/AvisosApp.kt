package com.sovero.avisoscampus

import android.app.Application
import com.sovero.avisoscampus.notificacion.Notificador

/**
 * La clase Application: se crea una sola vez, al iniciar la app, antes
 * que cualquier pantalla. Por eso es el lugar para crear el canal.
 */
class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanal(this)
    }
}
