package com.tecsup.mibodega.ui.cliente

/**
 * Todas las rutas de la app cliente en un solo lugar.
 * Así ninguna pantalla escribe el texto de la ruta "a mano"
 * y si una ruta cambia, solo se cambia aquí.
 */
object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INICIO = "inicio"
    const val DETALLE = "detalle/{productoId}"
    const val CARRITO = "carrito"
    const val ENTREGA = "entrega"
    const val CONFIRMACION = "confirmacion"

    // Destinos del menú inferior (NavigationBar), además de INICIO.
    const val CATEGORIAS = "categorias"
    const val PEDIDOS = "pedidos"
    const val PERFIL = "perfil"

    // Arma la ruta de detalle con el id del producto elegido.
    fun detalle(productoId: Int) = "detalle/$productoId"
}
