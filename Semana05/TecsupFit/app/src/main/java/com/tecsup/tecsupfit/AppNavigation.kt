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
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
