package com.sovero.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sovero.ayudacampus.data.prefs.PreferenciasStore
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.Rol
import com.sovero.ayudacampus.ui.cliente.DetalleTicketScreen
import com.sovero.ayudacampus.ui.cliente.LoginScreen
import com.sovero.ayudacampus.ui.cliente.MisTicketsScreen
import com.sovero.ayudacampus.ui.cliente.NuevoTicketScreen
import com.sovero.ayudacampus.ui.cliente.SplashScreen
import com.sovero.ayudacampus.ui.components.AppTopBar
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

@Composable
fun NavAyuda(repo: AyudaRepository) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { PreferenciasStore(context.applicationContext) }
    val factory = AyudaViewModelFactory(repo, prefs)

    // La sesion se comparte entre todas las pantallas
    val sesionVm: SesionViewModel = viewModel(factory = factory)

    // Vuelve al Login y limpia la pila de navegacion
    val cerrarSesion = {
        sesionVm.cerrarSesion()
        navController.navigate(Rutas.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
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
            AyudaTheme(rol = Rol.CLIENTE) {
                MisTicketsScreen(
                    vm = vm,
                    onAbrirTicket = { id -> navController.navigate(Rutas.detalleTicket(id)) },
                    onNuevoTicket = { navController.navigate(Rutas.NUEVO_TICKET) },
                    onCerrarSesion = cerrarSesion
                )
            }
        }

        composable(Rutas.NUEVO_TICKET) {
            val vm: NuevoTicketViewModel = viewModel(factory = factory)
            AyudaTheme(rol = Rol.CLIENTE) {
                NuevoTicketScreen(
                    vm = vm,
                    onAtras = { navController.popBackStack() },
                    onEnviado = { navController.popBackStack() }
                )
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
            AyudaTheme(rol = Rol.CLIENTE) {
                DetalleTicketScreen(vm = vm, onAtras = { navController.popBackStack() })
            }
        }

        // ---------- Soporte TI ----------

        composable(Rutas.PANEL) {
            val vm: PanelViewModel = viewModel(factory = factory)
            AyudaTheme(rol = Rol.ADMIN) {
                PanelScreen(
                    vm = vm,
                    onVerBandeja = { navController.navigate(Rutas.BANDEJA) },
                    onCategorias = { navController.navigate(Rutas.CATEGORIAS) },
                    onCerrarSesion = cerrarSesion
                )
            }
        }

        composable(Rutas.BANDEJA) {
            val vm: BandejaViewModel = viewModel(factory = factory)
            AyudaTheme(rol = Rol.ADMIN) {
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
            AyudaTheme(rol = Rol.ADMIN) {
                AtenderTicketScreen(
                    vm = vm,
                    onAtras = { navController.popBackStack() },
                    onGuardado = { navController.popBackStack() }
                )
            }
        }

        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = factory)
            AyudaTheme(rol = Rol.ADMIN) {
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
            AyudaTheme(rol = Rol.ADMIN) {
                FormularioCategoriaScreen(
                    vm = vm,
                    categoriaId = if (id == -1) null else id,
                    onListo = { navController.popBackStack() }
                )
            }
        }

        // TODO (Paso 12): Perfil, Ajustes y la NavigationBar de cada perfil
    }
}
