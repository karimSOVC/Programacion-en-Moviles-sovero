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

class Carrito(val nombreCliente: String) {
    private val productos = mutableListOf<Producto>()

    fun agregar(producto: Producto) {
        productos.add(producto)
        println("Producto agregado: ${producto.nombre}")
    }

    fun cantidad(): Int {
        return productos.size
    }

    fun calcularSubtotal(): Double {
        var subtotal = 0.0
        for (p in productos) {
            subtotal += p.importe()
        }
        return subtotal
    }

    fun calcularIGV(): Double {
        return calcularSubtotal() * 0.18
    }

    fun calcularTotal(): Double {
        return calcularSubtotal() + calcularIGV()
    }
}

fun main() {
    println("========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP   ")
    println("========================================")
    val carrito = Carrito("Juan Leon")
    println("Cliente: ${carrito.nombreCliente}")
    println()
    carrito.agregar(Producto("Laptop HP", 2500.0, 1))
    carrito.agregar(Producto("Mouse Logitech", 45.5, 2))
    carrito.agregar(Producto("Teclado Genius", 80.0, 3))
    carrito.agregar(Producto("Monitor Samsung", 900.0, 1))
    println()
    println("Cantidad de productos: ${carrito.cantidad()}")
    println()
    println(String.format("Subtotal    : S/ %8.2f", carrito.calcularSubtotal()))
    println(String.format("IGV (18%%)   : S/ %8.2f", carrito.calcularIGV()))
    println(String.format("TOTAL A PAGAR: S/ %8.2f", carrito.calcularTotal()))
}