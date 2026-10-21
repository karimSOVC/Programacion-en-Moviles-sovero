package com.sovero.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sovero.ayudacampus.model.Rol
import com.sovero.ayudacampus.ui.components.AyudaButton
import com.sovero.ayudacampus.ui.components.AyudaTextField
import com.sovero.ayudacampus.ui.theme.AyudaTheme
import com.sovero.ayudacampus.viewmodel.SesionViewModel

// Un solo Login para los dos perfiles: el color cambia segun la pestaña elegida
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(vm: SesionViewModel, onIngresar: (Rol) -> Unit) {
    var rol by remember { mutableStateOf(Rol.CLIENTE) }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    val error by vm.error.collectAsState()
    val usuario by vm.usuarioActual.collectAsState()
    val oscuro by vm.modoOscuro.collectAsState()

    // Cuando el login sale bien, el ViewModel llena usuarioActual y pasamos a la pantalla principal
    LaunchedEffect(usuario) {
        val u = usuario
        if (u != null) {
            onIngresar(Rol.valueOf(u.rol))
        }
    }

    AyudaTheme(rol = rol, oscuro = oscuro) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ingresa para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Selector de rol
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = rol == Rol.CLIENTE,
                        onClick = {
                            rol = Rol.CLIENTE
                            vm.limpiarError()
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("Estudiante")
                    }
                    SegmentedButton(
                        selected = rol == Rol.ADMIN,
                        onClick = {
                            rol = Rol.ADMIN
                            vm.limpiarError()
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("Soporte TI")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AyudaTextField(
                    valor = correo,
                    onCambio = { correo = it },
                    etiqueta = "Correo"
                )
                Spacer(modifier = Modifier.height(8.dp))
                AyudaTextField(
                    valor = clave,
                    onCambio = { clave = it },
                    etiqueta = "Contraseña",
                    esClave = true
                )

                val mensaje = error
                if (mensaje != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AyudaButton(
                    texto = "Ingresar",
                    onClick = { vm.login(correo, clave, rol) }
                )

                if (rol == Rol.ADMIN) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Modo práctica",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Puedes cambiar de perfil desde Ajustes en cualquier momento.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
