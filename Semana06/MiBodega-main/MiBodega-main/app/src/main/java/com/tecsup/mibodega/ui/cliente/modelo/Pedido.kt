package com.tecsup.mibodega.ui.cliente.modelo

data class Pedido(
    val id: Int,
    val items: List<ItemCarrito>,
    val total: Double,
    val direccion: String,
    val metodoPago: String
)
