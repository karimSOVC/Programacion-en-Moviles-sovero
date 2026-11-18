package com.sovero.campusgps.data

import com.sovero.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}
