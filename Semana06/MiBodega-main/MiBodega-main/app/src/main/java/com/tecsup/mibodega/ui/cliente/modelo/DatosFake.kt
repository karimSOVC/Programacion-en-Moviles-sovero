package com.tecsup.mibodega.ui.cliente.modelo

/**
 * Datos de ejemplo (fake) para mostrar la UI sin base de datos.
 * Cuando conecten Room o una API, este archivo se reemplaza por
 * un Repository real, pero las pantallas no cambian porque ya
 * reciben una List<Producto> como parámetro.
 */
val listaCategorias = listOf("Todos", "Bebidas", "Abarrotes", "Snacks")

val listaProductosFake = listOf(
    Producto(
        id = 1,
        nombre = "Arroz Costeño",
        descripcion = "Arroz extra, grano largo, ideal para el día a día.",
        precio = 4.50,
        categoria = "Abarrotes"
    ),
    Producto(
        id = 2,
        nombre = "Aceite Primor",
        descripcion = "Aceite vegetal 1 L, alto en vitamina E.",
        precio = 8.90,
        categoria = "Abarrotes"
    ),
    Producto(
        id = 3,
        nombre = "Leche Gloria",
        descripcion = "Leche evaporada entera 1 L.",
        precio = 5.20,
        categoria = "Abarrotes"
    ),
    Producto(
        id = 4,
        nombre = "Galleta Oreo",
        descripcion = "Galletas de chocolate rellenas 126 g.",
        precio = 3.50,
        categoria = "Snacks"
    ),
    Producto(
        id = 5,
        nombre = "Coca-Cola Original",
        descripcion = "Bebida gaseosa sabor cola. Ideal para compartir en familia.",
        precio = 6.50,
        categoria = "Bebidas"
    ),
    Producto(
        id = 6,
        nombre = "Inca Kola",
        descripcion = "Bebida gaseosa de sabor nacional 1.5 L.",
        precio = 7.00,
        categoria = "Bebidas"
    ),
    Producto(
        id = 7,
        nombre = "Agua San Luis",
        descripcion = "Agua de mesa sin gas 625 ml.",
        precio = 2.00,
        categoria = "Bebidas"
    ),
    Producto(
        id = 8,
        nombre = "Papas Lay's Clásicas",
        descripcion = "Papas fritas con sal, bolsa de 70 g.",
        precio = 2.50,
        categoria = "Snacks"
    ),
    Producto(
        id = 9,
        nombre = "Azúcar Rubia Cartavio",
        descripcion = "Azúcar rubia de caña, bolsa de 1 kg.",
        precio = 4.20,
        categoria = "Abarrotes"
    ),
    Producto(
        id = 10,
        nombre = "Fideos Don Vittorio",
        descripcion = "Spaghetti de trigo, bolsa de 500 g.",
        precio = 3.80,
        categoria = "Abarrotes"
    ),
    Producto(
        id = 11,
        nombre = "Chocolate Sublime",
        descripcion = "Chocolate con leche y maní 30 g.",
        precio = 2.00,
        categoria = "Snacks"
    ),
    Producto(
        id = 12,
        nombre = "Frugos del Valle",
        descripcion = "Néctar de durazno 1 L.",
        precio = 5.50,
        categoria = "Bebidas"
    )
)

