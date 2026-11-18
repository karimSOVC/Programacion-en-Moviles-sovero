package com.sovero.centrotareas.model

sealed interface PrimosUi {
    data object Inicial : PrimosUi
    data object Calculando : PrimosUi
    data class Listo(val cantidad: Int, val milisegundos: Long) : PrimosUi
    data object Cancelado : PrimosUi
}

sealed interface CargaUi {
    data object Inicial : CargaUi
    data object Cargando : CargaUi
    data class Exito(
        val modo: String,
        val perfil: String,
        val pedidos: Int,
        val milisegundos: Long
    ) : CargaUi
    data class Error(val mensaje: String) : CargaUi
}

/**
 * Mejora (Fase 2): tiempos (en ms) de cada carga que terminó bien,
 * separados por modo, para comparar "Una por una" contra "A la vez".
 */
data class ComparacionTiempos(
    val unaPorUna: List<Long> = emptyList(),
    val aLaVez: List<Long> = emptyList()
) {
    val promedioUnaPorUna: Long?
        get() = if (unaPorUna.isEmpty()) null else unaPorUna.average().toLong()

    val promedioALaVez: Long?
        get() = if (aLaVez.isEmpty()) null else aLaVez.average().toLong()

    /** Cuántos ms se ahorran en promedio con "A la vez" (null si falta un modo). */
    val ahorro: Long?
        get() {
            val uno = promedioUnaPorUna ?: return null
            val vez = promedioALaVez ?: return null
            return uno - vez
        }
}
