package com.tecsup.mibodega.ui.cliente

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.mibodega.ui.cliente.modelo.COSTO_DELIVERY
import com.tecsup.mibodega.ui.cliente.modelo.ItemCarrito
import com.tecsup.mibodega.ui.cliente.modelo.Pedido
import com.tecsup.mibodega.ui.cliente.modelo.Producto
import com.tecsup.mibodega.ui.cliente.modelo.listaProductosFake
import com.tecsup.mibodega.ui.cliente.screens.bienvenida.BienvenidaScreen
import com.tecsup.mibodega.ui.cliente.screens.carrito.CarritoScreen
import com.tecsup.mibodega.ui.cliente.screens.categorias.CategoriasScreen
import com.tecsup.mibodega.ui.cliente.screens.confirmacion.ConfirmacionScreen
import com.tecsup.mibodega.ui.cliente.screens.detalle.DetalleProductoScreen
import com.tecsup.mibodega.ui.cliente.screens.entrega.DatosEntregaScreen
import com.tecsup.mibodega.ui.cliente.screens.inicio.InicioScreen
import com.tecsup.mibodega.ui.cliente.screens.login.LoginScreen
import com.tecsup.mibodega.ui.cliente.screens.pedidos.PedidosScreen
import com.tecsup.mibodega.ui.cliente.screens.perfil.PerfilScreen
import com.tecsup.mibodega.ui.cliente.screens.registro.RegistroScreen

/**
 * "Director de orquesta" de la app cliente:
 * - Tiene el NavHost con las rutas de cada pantalla (las rutas están en Rutas.kt).
 * - Tiene el estado del carrito (List<ItemCarrito>), que se reparte
 *   hacia abajo a Inicio, Detalle, Carrito y Entrega.
 * Ninguna Screen navega sola ni modifica el carrito directamente:
 * todas reciben funciones (lambdas) desde aquí (state hoisting).
 */
@Composable
fun ClienteApp() {
    val navController = rememberNavController()

    // El carrito vive aquí arriba, no en ninguna Screen.
    var carrito by remember { mutableStateOf<List<ItemCarrito>>(emptyList()) }

    // Historial de pedidos en memoria
    var pedidos by remember { mutableStateOf<List<Pedido>>(emptyList()) }

    // Resumen del último pedido confirmado, para mostrarlo en Confirmación
    // (el carrito ya se vació cuando esa pantalla aparece).
    var totalPedido by remember { mutableStateOf(0.0) }
    var direccionPedido by remember { mutableStateOf("") }
    var pagoPedido by remember { mutableStateOf("") }

    // Navegación del menú inferior (NavigationBar). Inicio queda siempre como
    // base de la pila: así no se acumulan pantallas al cambiar de sección y
    // el botón atrás regresa a Inicio.
    val irASeccion: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.INICIO)
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Rutas.BIENVENIDA
    ) {
        composable(Rutas.BIENVENIDA) {
            BienvenidaScreen(
                onRegistrarse = { navController.navigate(Rutas.REGISTRO) },
                onIniciarSesion = { navController.navigate(Rutas.LOGIN) },
                onTerminos = { /* TODO: abrir términos y condiciones */ }
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                onVolver = { navController.popBackStack() },
                onIngresar = { telefono ->
                    // TODO: validar el teléfono cuando exista el registro real
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                    }
                },
                onIrARegistro = {
                    // Se quita Login de la pila para que "volver" regrese a Bienvenida.
                    navController.navigate(Rutas.REGISTRO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onVolver = { navController.popBackStack() },
                onCrearCuenta = { nombre, telefono, direccion, referencia ->
                    // TODO: guardar estos datos cuando exista el registro real
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.BIENVENIDA) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.INICIO) {
            InicioScreen(
                cantidadCarrito = carrito.sumOf { it.cantidad },
                onVerCarrito = { navController.navigate(Rutas.CARRITO) },
                onProductoClick = { producto ->
                    navController.navigate(Rutas.detalle(producto.id))
                },
                onAgregarProducto = { producto ->
                    carrito = agregarOSumarProducto(carrito, producto, 1)
                },
                onNavegar = irASeccion
            )
        }

        composable(Rutas.CATEGORIAS) {
            CategoriasScreen(onNavegar = irASeccion)
        }

        composable(Rutas.PEDIDOS) {
            PedidosScreen(pedidos = pedidos, onNavegar = irASeccion)
        }

        composable(Rutas.PERFIL) {
            PerfilScreen(
                onNavegar = irASeccion,
                onCerrarSesion = {
                    carrito = emptyList()
                    pedidos = emptyList()
                    navController.navigate(Rutas.BIENVENIDA) {
                        popUpTo(Rutas.INICIO) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            val producto = listaProductosFake.first { it.id == productoId }

            DetalleProductoScreen(
                producto = producto,
                onVolver = { navController.popBackStack() },
                onAgregarAlCarrito = { productoSeleccionado, cantidad ->
                    carrito = agregarOSumarProducto(carrito, productoSeleccionado, cantidad)
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.CARRITO) {
            CarritoScreen(
                carrito = carrito,
                onVolver = { navController.popBackStack() },
                onIncrementar = { producto ->
                    carrito = carrito.map {
                        if (it.producto.id == producto.id) it.copy(cantidad = it.cantidad + 1) else it
                    }
                },
                onDecrementar = { producto ->
                    carrito = carrito.mapNotNull {
                        when {
                            it.producto.id != producto.id -> it
                            it.cantidad > 1 -> it.copy(cantidad = it.cantidad - 1)
                            else -> null // si llega a 0, se elimina de la lista
                        }
                    }
                },
                onEliminar = { producto ->
                    carrito = carrito.filterNot { it.producto.id == producto.id }
                },
                onContinuarPedido = { navController.navigate(Rutas.ENTREGA) }
            )
        }

        composable(Rutas.ENTREGA) {
            // remember: el total se calcula una vez al entrar. Así no cambia en
            // pantalla cuando el carrito se vacía al confirmar el pedido.
            val total = remember {
                carrito.sumOf { it.producto.precio * it.cantidad } + COSTO_DELIVERY
            }

            DatosEntregaScreen(
                total = total,
                onVolver = { navController.popBackStack() },
                onConfirmarPedido = { direccion, metodoPago ->
                    val nuevoPedido = Pedido(
                        id = pedidos.size + 1,
                        items = carrito,
                        total = total,
                        direccion = direccion,
                        metodoPago = metodoPago
                    )
                    pedidos = listOf(nuevoPedido) + pedidos

                    totalPedido = total
                    direccionPedido = direccion
                    pagoPedido = metodoPago
                    carrito = emptyList()

                    // popUpTo(INICIO): saca Carrito y Datos de entrega de la pila.
                    // Así, desde Confirmación el botón atrás lleva a Inicio y no
                    // se puede regresar a un pedido que ya se confirmó.
                    navController.navigate(Rutas.CONFIRMACION) {
                        popUpTo(Rutas.INICIO)
                    }
                }
            )
        }

        composable(Rutas.CONFIRMACION) {
            ConfirmacionScreen(
                total = totalPedido,
                direccion = direccionPedido,
                metodoPago = pagoPedido,
                onVolverAlInicio = {
                    navController.popBackStack(Rutas.INICIO, inclusive = false)
                }
            )
        }
    }
}

/**
 * Si el producto ya está en el carrito, le suma la cantidad;
 * si no, lo agrega como un ItemCarrito nuevo.
 */
private fun agregarOSumarProducto(
    carrito: List<ItemCarrito>,
    producto: Producto,
    cantidad: Int
): List<ItemCarrito> {
    val itemExistente = carrito.find { it.producto.id == producto.id }
    return if (itemExistente != null) {
        carrito.map {
            if (it.producto.id == producto.id) it.copy(cantidad = it.cantidad + cantidad) else it
        }
    } else {
        carrito + ItemCarrito(producto = producto, cantidad = cantidad)
    }
}