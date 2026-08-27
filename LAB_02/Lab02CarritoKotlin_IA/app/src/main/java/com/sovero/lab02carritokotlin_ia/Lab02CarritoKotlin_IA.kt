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
        println(">> '${producto.nombre}' x${producto.cantidad} agregado al carrito.")
    }

    fun estaVacio(): Boolean = productos.isEmpty()

    fun cantidad(): Int = productos.size

    fun calcularSubtotal(): Double {
        var subtotal = 0.0
        for (p in productos) subtotal += p.importe()
        return subtotal
    }

    fun calcularIGV(): Double = calcularSubtotal() * 0.18

    fun calcularTotal(): Double = calcularSubtotal() + calcularIGV()

    fun calcularDescuento(): Double {
        val total = calcularTotal()
        return when {
            total > 5000 -> total * 0.10
            total > 3000 -> total * 0.05
            else -> 0.0
        }
    }

    fun productoMasCaro(): Producto? = productos.maxByOrNull { it.precio }

    fun mostrarDetalle() {
        if (estaVacio()) {
            println(">> El carrito esta vacio.")
            return
        }
        println("---------- DETALLE DEL CARRITO ----------")
        var i = 1
        for (p in productos) {
            println(String.format("%d. %-20s x%d S/ %8.2f", i, p.nombre, p.cantidad, p.importe()))
            i++
        }
        println("-----------------------------------------")
    }

    fun mostrarResumenFinal() {
        if (estaVacio()) {
            println(">> No has comprado nada aun.")
            return
        }
        println()
        println("========== RESUMEN DE COMPRA ==========")
        mostrarDetalle()
        println("Cantidad de productos: ${cantidad()}")
        val subtotal = calcularSubtotal()
        val igv = calcularIGV()
        val total = calcularTotal()
        val descuento = calcularDescuento()
        println(String.format("Subtotal           : S/ %8.2f", subtotal))
        println(String.format("IGV (18%%)          : S/ %8.2f", igv))
        println(String.format("TOTAL A PAGAR      : S/ %8.2f", total))
        val masCaro = productoMasCaro()
        if (masCaro != null) {
            println("Producto mas caro  : ${masCaro.nombre} " +
                    String.format("(S/ %.2f)", masCaro.precio))
        }
        println(String.format("Descuento aplicado : S/ %8.2f", descuento))
        println(String.format("TOTAL CON DESCUENTO: S/ %8.2f", total - descuento))
        println("=======================================")
    }
}

fun main() {
    val catalogo = mutableListOf(
        Producto("Laptop HP", 2500.0, 0),
        Producto("Mouse Logitech", 45.5, 0),
        Producto("Teclado Genius", 80.0, 0),
        Producto("Monitor Samsung", 900.0, 0),
        Producto("Audifonos Sony", 350.0, 0)
    )

    println("========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP   ")
    println("========================================")
    print("Ingrese el nombre del cliente: ")
    val nombreCliente = readLine() ?: "Cliente"
    val carrito = Carrito(nombreCliente)
    println("Bienvenido, $nombreCliente!")

    var opcion: Int
    do {
        println()
        println("============= MENU =============")
        println("1. Ver catalogo y comprar")
        println("2. Agregar nuevo producto al catalogo")
        println("3. Ver mi carrito")
        println("4. Finalizar compra")
        println("5. Salir")
        print("Elija una opcion: ")
        opcion = readLine()?.toIntOrNull() ?: 0

        when (opcion) {
            1 -> {
                println()
                println("---------- CATALOGO ----------")
                for ((index, p) in catalogo.withIndex()) {
                    println(String.format("%d. %-20s S/ %8.2f", index + 1, p.nombre, p.precio))
                }
                println("------------------------------")
                print("Numero del producto a comprar: ")
                val num = readLine()?.toIntOrNull() ?: 0
                if (num in 1..catalogo.size) {
                    val seleccionado = catalogo[num - 1]
                    print("Cuantas unidades? ")
                    val cant = readLine()?.toIntOrNull() ?: 1
                    if (cant > 0) {
                        carrito.agregar(Producto(seleccionado.nombre, seleccionado.precio, cant))
                    } else {
                        println(">> Cantidad invalida.")
                    }
                } else {
                    println(">> Producto no valido.")
                }
            }
            2 -> {
                print("Nombre del nuevo producto: ")
                val nombre = readLine() ?: ""
                print("Precio: ")
                val precio = readLine()?.toDoubleOrNull() ?: 0.0
                if (nombre.isNotBlank() && precio > 0) {
                    catalogo.add(Producto(nombre, precio, 0))
                    println(">> Producto '$nombre' agregado al catalogo.")
                } else {
                    println(">> Datos invalidos.")
                }
            }
            3 -> carrito.mostrarDetalle()
            4 -> carrito.mostrarResumenFinal()
            5 -> println("Saliendo del sistema. Hasta luego!")
            else -> println(">> Opcion invalida.")
        }
    } while (opcion != 5)
}