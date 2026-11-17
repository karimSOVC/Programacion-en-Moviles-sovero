package com.sovero.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sovero.campusgps.model.UbicacionUi
import com.sovero.campusgps.util.formatearDistancia
import com.sovero.campusgps.viewmodel.CampusViewModelFactory
import com.sovero.campusgps.viewmodel.MapaViewModel

/**
 * Pantalla principal: botón para obtener la ubicación y el resultado.
 *
 * Solo dibuja según el estado del ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel = viewModel(factory = CampusViewModelFactory(LocalContext.current))
) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()

    // ---- Permiso de ubicación en tiempo de ejecución ----
    val contexto = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        // Si concedió FINE o COARSE, busca; si no, avisa sin cerrar la app
        if (resultado.values.any { it }) {
            vm.buscar()
        } else {
            vm.permisoDenegado()
        }
    }

    fun alPulsar() {
        val tieneFine = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val tieneCoarse = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tieneFine || tieneCoarse) {
            // Ya tiene permiso: busca sin preguntar
            vm.buscar()
        } else {
            // No tiene permiso: Android muestra el cuadro para pedirlo
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // El mapa ocupa todo el espacio libre de arriba
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Botón principal (relleno)
                    Button(
                        onClick = { alPulsar() },
                        enabled = estado !is UbicacionUi.Buscando
                    ) {
                        Text("Obtener mi ubicación")
                    }
                    // Botón secundario (con borde): deshabilitado hasta tener ubicación
                    OutlinedButton(
                        onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                        enabled = masCercano != null
                    ) {
                        Text("Ir al más cercano")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (val e = estado) {
                    UbicacionUi.Inicial -> Text("Pulsa el botón para ver dónde estás")
                    UbicacionUi.Buscando -> CircularProgressIndicator()
                    is UbicacionUi.Lista -> Text(
                        "Lat: ${e.posicion.latitud}  ·  Lon: ${e.posicion.longitud}"
                    )
                    is UbicacionUi.Error -> Text(
                        text = e.mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Texto con el lugar más cercano y su distancia
                masCercano?.let { lugar ->
                    val metros = lugares.firstOrNull { it.lugar.id == lugar.id }?.metros
                    Text(
                        text = "Más cercano: ${lugar.nombre}" +
                            (metros?.let { " (${formatearDistancia(it)})" } ?: ""),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Lista de lugares con altura fija debajo del mapa
            ListaLugares(
                lugares = lugares,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                onClick = { id -> vm.seleccionar(id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
    }
}
