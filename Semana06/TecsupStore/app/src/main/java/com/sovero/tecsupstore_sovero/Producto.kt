package com.sovero.tecsupstore_sovero

data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double
)

val productosLista = listOf(
    Producto(1, "Laptop Lenovo", "Tecnologia", 2499.0),
    Producto(2, "Mouse inalambrico", "Tecnologia", 59.9),
    Producto(3, "Polo Tecsup", "Ropa", 45.0),
    Producto(4, "Mochila", "Accesorios", 120.0),
    Producto(5, "Cuaderno A4", "Utiles", 12.5)
)
