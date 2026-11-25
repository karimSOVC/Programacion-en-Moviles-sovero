package com.sovero.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sovero.murocampus.data.PostRepositoryRemote
import com.sovero.murocampus.data.remote.RetrofitClient
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

/** Barra azul MuroCampus y, debajo, la pantalla de la lista. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(vm: PostsViewModel) {
    Scaffold(
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
        PantallaLista(
            vm = vm,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}
