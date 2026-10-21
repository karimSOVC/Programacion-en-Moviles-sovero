package com.sovero.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sovero.ayudacampus.data.prefs.PreferenciasStore
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.Rol
import com.sovero.ayudacampus.ui.cliente.LoginScreen
import com.sovero.ayudacampus.ui.cliente.MisTicketsScreen
import com.sovero.ayudacampus.ui.cliente.SplashScreen
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.soporte.CategoriasScreen
import com.sovero.ayudacampus.ui.theme.AyudaTheme
import com.sovero.ayudacampus.viewmodel.AyudaViewModelFactory
import com.sovero.ayudacampus.viewmodel.CategoriasViewModel
import com.sovero.ayudacampus.viewmodel.MisTicketsViewModel
import com.sovero.ayudacampus.viewmodel.SesionViewModel

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

        composable(Rutas.MIS_TICKETS) {
            val vm: MisTicketsViewModel = viewModel(factory = factory)
            AyudaTheme(rol = Rol.CLIENTE) {
                MisTicketsScreen(
                    vm = vm,
                    onAbrirTicket = { id ->
                        // TODO (Paso 9): navController.navigate(Rutas.detalleTicket(id))
                    },
                    onNuevoTicket = {
                        // TODO (Paso 8): navController.navigate(Rutas.NUEVO_TICKET)
                    },
                    onCerrarSesion = cerrarSesion
                )
            }
        }

        // TODO (Paso 10): reemplazar por PanelScreen
        composable(Rutas.PANEL) {
            PantallaTemporal(
                rol = Rol.ADMIN,
                titulo = "Panel",
                onCerrarSesion = cerrarSesion,
                onCategorias = { navController.navigate(Rutas.CATEGORIAS) }
            )
        }

        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = factory)
            Scaffold(
                topBar = {
                    AppTopBar(titulo = "Categorías", onAtras = { navController.popBackStack() })
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    CategoriasScreen(
                        vm = vm,
                        onEditar = { /* TODO (Paso 11): abrir FormularioCategoriaScreen */ }
                    )
                }
            }
        }

        // TODO (Pasos 8 y 9): Nuevo ticket y Detalle del ticket
        // TODO (Pasos 10 y 11): Bandeja, Atender ticket y Formulario de categoria
        // TODO (Paso 12): Perfil, Ajustes y la NavigationBar de cada perfil
    }
}

// Pantalla provisional para probar el login hasta construir Mis tickets y el Panel
@Composable
private fun PantallaTemporal(
    rol: Rol,
    titulo: String,
    onCerrarSesion: () -> Unit,
    onCategorias: (() -> Unit)? = null
) {
    AyudaTheme(rol = rol) {
        Scaffold(topBar = { AppTopBar(titulo = titulo) }) { padding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                if (onCategorias != null) {
                    Button(onClick = onCategorias) {
                        Text("Ver categorías")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedButton(onClick = onCerrarSesion) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}
