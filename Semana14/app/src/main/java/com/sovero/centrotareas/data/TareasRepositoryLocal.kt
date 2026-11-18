package com.sovero.centrotareas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.IOException

class TareasRepositoryLocal : TareasRepository {

    override suspend fun cargarPerfil(): String {
        delay(2000)
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        delay(3000)
        if (simularError) throw IOException("Sin conexión")
        return listOf("Pedido 101", "Pedido 102", "Pedido 103")
    }

    /**
     * Paso 5: cuenta los primos de 2 a [hasta]. Es un cálculo pesado, así que
     * corre en Dispatchers.Default (hilos pensados para usar el procesador).
     */
    override suspend fun contarPrimos(
        hasta: Int,
        alAvanzar: (Float) -> Unit
    ): Int = withContext(Dispatchers.Default) {
        var cantidad = 0
        // Mejora (Fase 2): avisa el avance cada 1 % (no en cada número,
        // para no llenar la pantalla de actualizaciones)
        val paso = maxOf(hasta / 100, 1)
        alAvanzar(0f)
        for (n in 2..hasta) {
            // Si cancelaron la corrutina, aquí se detiene el cálculo
            ensureActive()
            if (esPrimo(n)) cantidad++
            if (n % paso == 0) alAvanzar(n.toFloat() / hasta)
        }
        alAvanzar(1f)
        cantidad
    }

    /** true si ningún d entre 2 y la raíz de n divide a n. */
    private fun esPrimo(n: Int): Boolean {
        var d = 2
        // d * d <= n es lo mismo que d <= raíz de n (con Long para no desbordar)
        while (d.toLong() * d <= n) {
            if (n % d == 0) return false
            d++
        }
        return true
    }
}
