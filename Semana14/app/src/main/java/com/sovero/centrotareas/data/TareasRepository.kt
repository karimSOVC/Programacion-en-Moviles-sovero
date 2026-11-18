package com.sovero.centrotareas.data

interface TareasRepository {
    /**
     * Mejora (Fase 2): [alAvanzar] recibe el avance del cálculo, de 0f a 1f.
     * Si no se pasa, el cálculo funciona igual que en la Fase 1.
     */
    suspend fun contarPrimos(hasta: Int, alAvanzar: (Float) -> Unit = {}): Int
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}
