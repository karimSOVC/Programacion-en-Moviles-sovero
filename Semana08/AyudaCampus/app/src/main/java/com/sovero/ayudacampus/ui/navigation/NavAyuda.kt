package com.sovero.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sovero.ayudacampus.data.prefs.PreferenciasStore
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.Rol
import com.sovero.ayudacampus.ui.cliente.DetalleTicketScreen
import com.sovero.ayudacampus.ui.cliente.LoginScreen
import com.sovero.ayudacampus.ui.cliente.MisTicketsScreen
import com.sovero.ayudacampus.ui.cliente.NuevoTicketScreen
import com.sovero.ayudacampus.ui.cliente.PerfilScreen
import com.sovero.ayudacampus.ui.cliente.SplashScreen
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.soporte.AjustesScreen
import com.sovero.ayudacampus.ui.soporte.AtenderTicketScreen
import com.sovero.ayudacampus.ui.soporte.BandejaScreen
import com.sovero.ayudacampus.ui.soporte.CategoriasScreen
import com.sovero.ayudacampus.ui.soporte.FormularioCategoriaScreen
import com.sovero.ayudacampus.ui.soporte.PanelScreen
import com.sovero.ayudacampus.ui.theme.AyudaTheme
import com.sovero.ayudacampus.viewmodel.AtenderViewModel
import com.sovero.ayudacampus.viewmodel.AyudaViewModelFactory
import com.sovero.ayudacampus.viewmodel.BandejaViewModel
import com.sovero.ayudacampus.viewmodel.CategoriasViewModel
import com.sovero.ayudacampus.viewmodel.MisTicketsViewModel
import com.sovero.ayudacampus.viewmodel.NuevoTicketViewModel
import com.sovero.ayudacampus.viewmodel.PanelViewModel
import com.sovero.ayudacampus.viewmodel.SesionViewModel
import com.sovero.ayudacampus.viewmodel.TicketDetalleViewModel

// Un item de la barra de navegacion de abajo
private data class ItemBarra(val ruta: String, val etiqueta: String, val icono: ImageVector)

// 3 items para el estudiante y 4 para Soporte TI
private val itemsEstudiante = listOf(
    ItemBarra(Rutas.MIS_TICKETS, "Tickets", Icons.AutoMirrored.Filled.List),
    ItemBarra(Rutas.NUEVO_TICKET, "Nuevo", Icons.Filled.Add),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)
private val itemsSoporte = listOf(
    ItemBarra(Rutas.PANEL, "Panel", Icons.Filled.BarChart),
    ItemBarra(Rutas.BANDEJA, "Tickets", Icons.AutoMirrored.Filled.List),
    ItemBarra(Rutas.CATEGORIAS, "Categorías", Icons.Filled.Sell),
    ItemBarra(Rutas.AJUSTES, "Ajustes", Icons.Filled.Settings)
)

@Composable
fun NavAyuda(repo: AyudaRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { PreferenciasStore(context.applicationContext) }
    val factory = AyudaViewModelFactory(repo, prefs)

    // La sesion se comparte entre todas las pantallas
    val sesionVm: SesionViewModel = viewModel(factory = factory)
    val oscuro by sesionVm.modoOscuro.collectAsState()
    val recibirTickets by sesionVm.recibirTickets.collectAsState()

    // Vuelve al Login y limpia la pila de navegacion
    val cerrarSesion = {
        sesionVm.cerrarSesion()
        navController.navigate(Rutas.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    // Guarda el otro perfil y navega a su grafo, limpiando la pila (popUpTo)
    val cambiarPerfil = {
        sesionVm.cambiarPerfil { nuevoRol ->
            val destino = if (nuevoRol == Rol.ADMIN) Rutas.PANEL else Rutas.MIS_TICKETS
            navController.navigate(destino) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    // Que barra mostrar segun la pantalla actual
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val esEstudiante = itemsEstudiante.any { it.ruta == rutaActual }
    val esSoporte = itemsSoporte.any { it.ruta == rutaActual }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (esEstudiante || esSoporte) {
                val items = if (esEstudiante) itemsEstudiante else itemsSoporte
                val inicio = if (esEstudiante) Rutas.MIS_TICKETS else Rutas.PANEL
                AyudaTheme(rol = if (esEstudiante) Rol.CLIENTE else Rol.ADMIN, oscuro = oscuro) {
                    NavigationBar {
                        items.forEach { item ->
                            NavigationBarItem(
                                selected = rutaActual == item.ruta,
                                onClick = {
                                    navController.navigate(item.ruta) {
                                        popUpTo(inicio)
                                        launchSingleTop = true
                                    }
                                },
                                icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                                label = { Text(item.etiqueta) }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingBarra ->
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier
                .padding(paddingBarra)
                .consumeWindowInsets(paddingBarra)
        ) {

            composable(Rutas.SPLASH) {
                AyudaTheme(rol = Rol.ADMIN, oscuro = oscuro) {
                    SplashScreen(onTerminar = {
                        // Si ya hay un perfil guardado, salta el login
                        val destino = when (sesionVm.perfilActivo.value) {
                            Rol.CLIENTE.name -> Rutas.MIS_TICKETS
                            Rol.ADMIN.name -> Rutas.PANEL
                            else -> Rutas.LOGIN
                        }
                        navController.navigate(destino) {
                            popUpTo(Rutas.SPLASH) { inclusive = true }
                        }
                    })
                }
            }

            composable(Rutas.LOGIN) {
                LoginScreen(vm = sesionVm, onIngresar = { rol ->
                    val destino = if (rol == Rol.ADMIN) Rutas.PANEL else Rutas.MIS_TICKETS
                    navController.navigate(destino) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                })
            }

            // ---------- Estudiante ----------

            composable(Rutas.MIS_TICKETS) {
                val vm: MisTicketsViewModel = viewModel(factory = factory)
                AyudaTheme(rol = Rol.CLIENTE, oscuro = oscuro) {
                    MisTicketsScreen(
                        vm = vm,
                        onAbrirTicket = { id -> navController.navigate(Rutas.detalleTicket(id)) },
                        onNuevoTicket = { navController.navigate(Rutas.NUEVO_TICKET) }
                    )
                }
            }

            composable(Rutas.NUEVO_TICKET) {
                AyudaTheme(rol = Rol.CLIENTE, oscuro = oscuro) {
                    if (recibirTickets) {
                        val vm: NuevoTicketViewModel = viewModel(factory = factory)
                        NuevoTicketScreen(
                            vm = vm,
                            onAtras = { navController.popBackStack() },
                            onEnviado = { navController.popBackStack() }
                        )
                    } else {
                        // Si Soporte apago "Recibir tickets", no se pueden crear tickets
                        MesaCerrada(onAtras = { navController.popBackStack() })
                    }
                }
            }

            composable(
                route = Rutas.DETALLE_TICKET,
                arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("ticketId") ?: 0
                val vm: TicketDetalleViewModel = viewModel(factory = factory)
                LaunchedEffect(id) {
                    vm.cargar(id)
                }
                AyudaTheme(rol = Rol.CLIENTE, oscuro = oscuro) {
                    DetalleTicketScreen(vm = vm, onAtras = { navController.popBackStack() })
                }
            }

            composable(Rutas.PERFIL) {
                val ticketsVm: MisTicketsViewModel = viewModel(factory = factory)
                AyudaTheme(rol = Rol.CLIENTE, oscuro = oscuro) {
                    PerfilScreen(
                        sesionVm = sesionVm,
                        ticketsVm = ticketsVm,
                        onCambiarPerfil = cambiarPerfil,
                        onCerrarSesion = cerrarSesion
                    )
                }
            }

            // ---------- Soporte TI ----------

            composable(Rutas.PANEL) {
                val vm: PanelViewModel = viewModel(factory = factory)
                AyudaTheme(rol = Rol.ADMIN, oscuro = oscuro) {
                    PanelScreen(
                        vm = vm,
                        onVerBandeja = { navController.navigate(Rutas.BANDEJA) }
                    )
                }
            }

            composable(Rutas.BANDEJA) {
                val vm: BandejaViewModel = viewModel(factory = factory)
                AyudaTheme(rol = Rol.ADMIN, oscuro = oscuro) {
                    BandejaScreen(
                        vm = vm,
                        onAtender = { id -> navController.navigate(Rutas.atenderTicket(id)) },
                        onAtras = { navController.popBackStack() }
                    )
                }
            }

            composable(
                route = Rutas.ATENDER_TICKET,
                arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt("ticketId") ?: 0
                val vm: AtenderViewModel = viewModel(factory = factory)
                LaunchedEffect(id) {
                    vm.cargar(id)
                }
                AyudaTheme(rol = Rol.ADMIN, oscuro = oscuro) {
                    AtenderTicketScreen(
                        vm = vm,
                        onAtras = { navController.popBackStack() },
                        onGuardado = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.CATEGORIAS) {
                val vm: CategoriasViewModel = viewModel(factory = factory)
                AyudaTheme(rol = Rol.ADMIN, oscuro = oscuro) {
                    Scaffold(
                        topBar = {
                            AppTopBar(titulo = "Categorías", onAtras = { navController.popBackStack() })
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { navController.navigate("formulario_categoria") },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Nueva categoría")
                            }
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            CategoriasScreen(
                                vm = vm,
                                onEditar = { id -> navController.navigate(Rutas.formularioCategoria(id)) }
                            )
                        }
                    }
                }
            }

            composable(
                route = Rutas.FORMULARIO_CATEGORIA,
                arguments = listOf(navArgument("categoriaId") {
                    type = NavType.IntType
                    defaultValue = -1 // -1 = categoria nueva
                })
            ) { entrada ->
                val id = entrada.arguments?.getInt("categoriaId") ?: -1
                val vm: CategoriasViewModel = viewModel(factory = factory)
                AyudaTheme(rol = Rol.ADMIN, oscuro = oscuro) {
                    FormularioCategoriaScreen(
                        vm = vm,
                        categoriaId = if (id == -1) null else id,
                        onListo = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.AJUSTES) {
                AyudaTheme(rol = Rol.ADMIN, oscuro = oscuro) {
                    AjustesScreen(
                        sesionVm = sesionVm,
                        onCambiarPerfil = cambiarPerfil,
                        onCerrarSesion = cerrarSesion
                    )
                }
            }
        }
    }
}

// Aviso para el estudiante cuando la mesa de ayuda no recibe tickets
@Composable
private fun MesaCerrada(onAtras: () -> Unit) {
    Scaffold(topBar = { AppTopBar(titulo = "Nuevo ticket", onAtras = onAtras) }) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Mesa de ayuda cerrada",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Por ahora Soporte TI no está recibiendo tickets. Inténtalo más tarde.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
