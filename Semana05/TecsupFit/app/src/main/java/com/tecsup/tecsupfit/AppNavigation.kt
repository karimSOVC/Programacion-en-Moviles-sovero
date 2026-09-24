package com.tecsup.tecsupfit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

data class PestanaBottomBar(val screen: Screen, val icono: ImageVector)

val pestanas = listOf(
    PestanaBottomBar(Screen.Inicio, Icons.Filled.Home),
    PestanaBottomBar(Screen.Reservas, Icons.Filled.DateRange),
    PestanaBottomBar(Screen.Rutinas, Icons.AutoMirrored.Filled.List),
    PestanaBottomBar(Screen.Perfil, Icons.Filled.Person)
)

val todasLasPantallas = listOf(
    Screen.Inicio, Screen.Reservas, Screen.Rutinas, Screen.Perfil,
    Screen.Detalle, Screen.Confirmacion
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val pantallaActual = todasLasPantallas.find { it.route == rutaActual }
    val esPestana = pestanas.any { it.screen.route == rutaActual }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = pantallaActual?.titulo ?: "TecsupFit") },
                navigationIcon = {
                    if (rutaActual == Screen.Detalle.route) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (esPestana) {
                NavigationBar {
                    pestanas.forEach { pestana ->
                        NavigationBarItem(
                            selected = rutaActual == pestana.screen.route,
                            onClick = {
                                navController.navigate(pestana.screen.route) {
                                    popUpTo(Screen.Inicio.route)
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = pestana.icono,
                                    contentDescription = pestana.screen.titulo
                                )
                            },
                            label = { Text(text = pestana.screen.titulo) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Inicio.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Inicio.route) {
                PantallaInicio(
                    onClaseClick = { clase ->
                        navController.navigate(Screen.Detalle.crearRuta(clase.id))
                    }
                )
            }
            composable(Screen.Reservas.route) {
                PantallaReservas()
            }
            composable(Screen.Rutinas.route) {
                PantallaRutinas()
            }
            composable(Screen.Perfil.route) {
                PantallaPerfil()
            }
            composable(
                route = Screen.Detalle.route,
                arguments = listOf(
                    navArgument(name = "claseId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val claseId = backStackEntry.arguments?.getInt("claseId") ?: 0
                PantallaDetalle(
                    claseId = claseId,
                    onReservar = { horarioIndex ->
                        navController.navigate(Screen.Confirmacion.crearRuta(claseId, horarioIndex))
                    }
                )
            }
            composable(
                route = Screen.Confirmacion.route,
                arguments = listOf(
                    navArgument(name = "claseId") { type = NavType.IntType },
                    navArgument(name = "horarioIndex") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val claseId = backStackEntry.arguments?.getInt("claseId") ?: 0
                val horarioIndex = backStackEntry.arguments?.getInt("horarioIndex") ?: 0
                PantallaConfirmacion(
                    claseId = claseId,
                    horarioIndex = horarioIndex,
                    onVerReservas = {
                        navController.popBackStack(Screen.Inicio.route, inclusive = false)
                    }
                )
            }
        }
    }
}
