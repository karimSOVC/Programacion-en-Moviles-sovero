package com.sovero.campusgps.util

import java.util.Locale

/**
 * Convierte metros a un texto para mostrar.
 * Menos de 1000: metros enteros, como "350 m".
 * Desde 1000: kilómetros con un decimal, como "1.5 km".
 */
fun formatearDistancia(metros: Float): String =
    if (metros < 1000) {
        "${metros.toInt()} m"
    } else {
        String.format(Locale.US, "%.1f km", metros / 1000)
    }
