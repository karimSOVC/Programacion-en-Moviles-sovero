package com.sovero.ayudacampus.ui.cliente

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.theme.ColorAbierto
import com.sovero.ayudacampus.ui.theme.ColorAlta
import com.sovero.ayudacampus.ui.theme.ColorResuelto
import com.sovero.ayudacampus.ui.theme.CianPrimario
import com.sovero.ayudacampus.viewmodel.MisTicketsViewModel
import com.sovero.ayudacampus.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(
    sesionVm: SesionViewModel,
    ticketsVm: MisTicketsViewModel,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario by sesionVm.usuarioActual.collectAsState()
    val modoOscuro by sesionVm.modoOscuro.collectAsState()
    val notificaciones by sesionVm.notificaciones.collectAsState()
    val abiertos by ticketsVm.cantidadAbiertos.collectAsState()
    val resueltos by ticketsVm.cantidadResueltos.collectAsState()
    val total by ticketsVm.cantidadTotal.collectAsState()

    Scaffold(topBar = { AppTopBar(titulo = "Perfil") }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Datos del usuario
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(56.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = usuario?.nombre ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = usuario?.correo ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Estudiante",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contadores de sus tickets
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ContadorPerfil(abiertos, "Abiertos", ColorAbierto, Modifier.weight(1f))
                ContadorPerfil(resueltos, "Resueltos", ColorResuelto, Modifier.weight(1f))
                ContadorPerfil(total, "Total", CianPrimario, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cambiar de perfil (modo practica)
            Card(
                onClick = onCambiarPerfil,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Cambiar de perfil", fontWeight = FontWeight.Bold)
                        Text(text = "Modo práctica: ir a vista Soporte TI", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preferencias
            Text(text = "Preferencias", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    FilaPreferencia(
                        titulo = "Modo oscuro",
                        detalle = "Cambia el tema de la app",
                        activo = modoOscuro,
                        onCambio = { sesionVm.guardarModoOscuro(it) }
                    )
                    FilaPreferencia(
                        titulo = "Notificaciones",
                        detalle = "Avisar cambios de estado",
                        activo = notificaciones,
                        onCambio = { sesionVm.guardarNotificaciones(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Se guardan en el dispositivo con DataStore",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cerrar sesion: borra el perfil y vuelve al Login
            OutlinedButton(
                onClick = onCerrarSesion,
                border = BorderStroke(1.dp, ColorAlta),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cerrar sesión", color = ColorAlta, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ContadorPerfil(numero: Int, etiqueta: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = numero.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(text = etiqueta, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}

@Composable
private fun FilaPreferencia(titulo: String, detalle: String, activo: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, fontWeight = FontWeight.SemiBold)
            Text(
                text = detalle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = activo, onCheckedChange = onCambio)
    }
}
