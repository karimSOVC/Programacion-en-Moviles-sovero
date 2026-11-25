package com.sovero.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sovero.murocampus.data.PostRepositoryRemote
import com.sovero.murocampus.data.remote.RetrofitClient
import com.sovero.murocampus.ui.PantallaCrear
import com.sovero.murocampus.ui.PantallaDetalle
import com.sovero.murocampus.ui.PantallaEditar
import com.sovero.murocampus.ui.PantallaLista
import com.sovero.murocampus.ui.theme.MuroCampusTheme
import com.sovero.murocampus.viewmodel.PostsViewModel
import com.sovero.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                // El ViewModel recibe el Repository; el Repository, la API
                val vm: PostsViewModel = viewModel(
                    factory = PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
                )
                PantallaPrincipal(vm)
            }
        }
    }
}

/**
 * Barra azul MuroCampus y, debajo, la lista o el detalle.
 * idSeleccionado = null muestra la lista; con un id, el detalle.
 * creando = true muestra el formulario para crear una publicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(vm: PostsViewModel) {
    var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }
    // Mejora (Fase 2): título que se está editando (null = no se edita)
    var tituloEditando by rememberSaveable { mutableStateOf<String?>(null) }

    // El botón Atrás del sistema: desde Editar vuelve al detalle;
    // desde el detalle o Crear vuelve a la lista (sin recargarla)
    BackHandler(enabled = idSeleccionado != null || creando) {
        if (tituloEditando != null) {
            tituloEditando = null
        } else {
            idSeleccionado = null
            creando = false
        }
    }

    Scaffold(
        // Paso 9: el botón + solo se ve en la lista y abre la pantalla Crear
        floatingActionButton = {
            if (idSeleccionado == null && !creando) {
                FloatingActionButton(onClick = {
                    vm.limpiarCreacion()
                    creando = true
                }) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("MuroCampus", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        val modificador = Modifier
            .fillMaxSize()
            .padding(padding)

        val id = idSeleccionado
        if (creando) {
            PantallaCrear(vm = vm, modifier = modificador)
        } else if (id == null) {
            PantallaLista(
                vm = vm,
                onAbrir = { idTocado ->
                    vm.abrir(idTocado)
                    idSeleccionado = idTocado
                },
                modifier = modificador
            )
        } else {
            val titulo = tituloEditando
            if (titulo != null) {
                PantallaEditar(vm = vm, id = id, tituloActual = titulo, modifier = modificador)
            } else {
                PantallaDetalle(
                    vm = vm,
                    id = id,
                    onEditar = { tituloDelPost ->
                        vm.limpiarEdicion()
                        tituloEditando = tituloDelPost
                    },
                    modifier = modificador
                )
            }
        }
    }
}
