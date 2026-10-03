package com.tecsup.mibodega.ui.cliente.modelo

data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int
)

// Costo fijo de envío. Lo usan Carrito y Datos de entrega para el total.
const val COSTO_DELIVERY = 4.00

