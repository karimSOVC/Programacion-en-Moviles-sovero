package com.tecsup.tecsupfit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Inicio.route,
        modifier = modifier
    ) {
        composable(Screen.Inicio.route) {
            PantallaInicio(
                onClaseClick = { clase ->
                    navController.navigate(Screen.Detalle.crearRuta(clase.id))
                }
            )
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
                },
                onVolver = { navController.popBackStack() }
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
                    // Por ahora vuelve a Inicio; en el commit 7 irá a la pantalla Reservas
                    navController.popBackStack(Screen.Inicio.route, inclusive = false)
                }
            )
        }
    }
}
