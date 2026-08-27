package com.sovero.lab02carritokotlin_ia

class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
) {
    fun importe(): Double {
        return precio * cantidad
    }
}

fun main() {
    println("========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP   ")
    println("========================================")
    val nombreCliente = "Juan Leon"
    val productos = mutableListOf<Producto>()
    println("Cliente: $nombreCliente")
    println()
    productos.add(Producto("Laptop HP", 2500.0, 1))
    productos.add(Producto("Mouse Logitech", 45.5, 2))
    productos.add(Producto("Teclado Genius", 80.0, 3))
    productos.add(Producto("Monitor Samsung", 900.0, 1))
    for (producto in productos) {
        println("Producto agregado: ${producto.nombre}")
    }
}