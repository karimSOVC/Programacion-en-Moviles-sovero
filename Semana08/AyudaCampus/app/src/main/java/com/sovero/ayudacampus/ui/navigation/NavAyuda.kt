package com.sovero.ayudacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.ui.cliente.SplashScreen
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.soporte.CategoriasScreen
import com.sovero.ayudacampus.viewmodel.AyudaViewModelFactory
import com.sovero.ayudacampus.viewmodel.CategoriasViewModel

@Composable
fun NavAyuda(repo: AyudaRepository) {
    val navController = rememberNavController()
    val factory = AyudaViewModelFactory(repo)

    NavHost(navController = navController, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            SplashScreen(onTerminar = {
                // TODO (Paso 6): ir al Login, o directo a la pantalla principal si hay perfil guardado
                navController.navigate(Rutas.CATEGORIAS) {
                    popUpTo(Rutas.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Rutas.CATEGORIAS) {
            val vm: CategoriasViewModel = viewModel(factory = factory)
            Scaffold(topBar = { AppTopBar(titulo = "Categorías") }) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    CategoriasScreen(
                        vm = vm,
                        onEditar = { /* TODO (Paso 11): abrir FormularioCategoriaScreen */ }
                    )
                }
            }
        }

        // TODO (Paso 6): Login
        // TODO (Pasos 7 al 9): Mis tickets, Nuevo ticket y Detalle del ticket
        // TODO (Pasos 10 y 11): Panel, Bandeja, Atender ticket y Formulario de categoria
        // TODO (Paso 12): Perfil, Ajustes y la NavigationBar de cada perfil
    }
}
