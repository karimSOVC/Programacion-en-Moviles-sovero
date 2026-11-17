package com.sovero.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sovero.campusgps.data.LugaresRepositoryLocal
import com.sovero.campusgps.data.UbicacionRepositoryGoogle

/**
 * Crea el MapaViewModel y le entrega los repositorios.
 *
 * El Context se usa aquí (para crear el repositorio de Google) y no llega
 * al ViewModel.
 */
class CampusViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            return MapaViewModel(
                ubicacionRepo = UbicacionRepositoryGoogle(context.applicationContext),
                lugaresRepo = LugaresRepositoryLocal()
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
