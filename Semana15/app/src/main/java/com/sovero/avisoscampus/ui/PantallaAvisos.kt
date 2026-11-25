package com.sovero.avisoscampus.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.material3.Switch
import androidx.compose.runtime.DisposableEffect
import androidx.core.content.ContextCompat
import com.sovero.avisoscampus.receiver.ModoAvionReceiver
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.sovero.avisoscampus.model.PermisoUi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sovero.avisoscampus.notificacion.Notificador
import com.sovero.avisoscampus.viewmodel.AvisosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    // Ya viene hecho: la pantalla recibe los eventos del ViewModel y,
    // como sí tiene Context, le pide al Notificador mostrar el aviso
    val contexto = LocalContext.current
    LaunchedEffect(Unit) {
        vm.eventos.collect { aviso ->
            Notificador.mostrar(contexto, aviso)
        }
    }

    val permiso by vm.permiso.collectAsState()
    val faltan by vm.faltan.collectAsState()
    val escuchando by vm.escuchandoAvion.collectAsState()
    val avisos by vm.avisos.collectAsState()

    // Paso 6: el receptor se registra solo mientras el switch está activado,
    // y se quita en onDispose (al apagar el switch o al salir de la pantalla)
    DisposableEffect(escuchando) {
        if (escuchando) {
            val receptor = ModoAvionReceiver { activo -> vm.alCambiarModoAvion(activo) }
            ContextCompat.registerReceiver(
                contexto,
                receptor,
                IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose { contexto.unregisterReceiver(receptor) }
        } else {
            onDispose { }
        }
    }

    // Paso 2: el cuadro del permiso lo dibuja Android; aquí llega la respuesta
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    // Al iniciar: si el permiso ya estaba concedido, se avisa al ViewModel
    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) vm.permisoRespondido(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AvisosCampus", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Tarjeta("Permiso de notificaciones") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Estado:")
                    EtiquetaPermiso(permiso)
                }
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Android 13 o más: aparece el cuadro de Android
                        lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // Versiones anteriores: el permiso ya viene dado
                        vm.permisoRespondido(true)
                    }
                }) {
                    Text("Permitir notificaciones")
                }
            }
            Tarjeta("Avisos") {
                Button(onClick = { vm.avisarAhora() }) {
                    Text("Mostrar aviso")
                }
            }
            Tarjeta("Recordatorio") {
                val corriendo = faltan != null
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Se deshabilita mientras corre la cuenta
                    Button(onClick = { vm.recordarEn(10) }, enabled = !corriendo) {
                        Text("Recordar en 10 s")
                    }
                    // Solo se puede cancelar mientras corre
                    OutlinedButton(onClick = { vm.cancelarRecordatorio() }, enabled = corriendo) {
                        Text("Cancelar")
                    }
                }
                faltan?.let { Text("Faltan $it s", fontWeight = FontWeight.Bold) }
            }
            Tarjeta("Receptor de anuncios") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Escuchar el modo avión")
                    Switch(
                        checked = escuchando,
                        onCheckedChange = { vm.cambiarEscucha(it) }
                    )
                }
                Text(
                    text = "Activa o desactiva el modo avión del celular",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Paso 7: todos los avisos, con lo más reciente arriba
            HistorialAvisos(avisos)
        }
    }
}

/** Etiqueta de color: Sin pedir (gris), Permitido (verde) o Denegado (rojo). */
@Composable
private fun EtiquetaPermiso(permiso: PermisoUi) {
    val (texto, fondo, letra) = when (permiso) {
        PermisoUi.SinPedir -> Triple("Sin pedir", Color(0xFFE0E0E0), Color(0xFF424242))
        PermisoUi.Concedido -> Triple("Permitido", Color(0xFFDFF3E1), Color(0xFF1B5E20))
        PermisoUi.Denegado -> Triple("Denegado", Color(0xFFFCDCDC), Color(0xFFB71C1C))
    }
    Surface(color = fondo, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = texto,
            color = letra,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

/** Card con el título en azul; el contenido va centrado debajo. */
@Composable
fun Tarjeta(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            contenido()
        }
    }
}
