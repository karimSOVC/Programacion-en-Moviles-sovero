package com.sovero.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sovero.centrotareas.model.CargaUi
import com.sovero.centrotareas.model.PrimosUi
import com.sovero.centrotareas.ui.theme.VerdeFondo
import com.sovero.centrotareas.ui.theme.VerdeTexto
import com.sovero.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    val primos by vm.primos.collectAsState()
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    // Texto del campo y el último N que se mandó a contar
    var textoN by rememberSaveable { mutableStateOf("500000") }
    var ultimoN by rememberSaveable { mutableIntStateOf(0) }
    // Contador de toques: es solo de la pantalla
    var toques by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Tarjeta("Tarea con progreso") {
                // Prueba de vida: si deja de sumar, la pantalla está congelada
                Button(onClick = { toques++ }) {
                    Text("Tócame: $toques")
                }
                // Iniciar solo si no corre; Cancelar solo mientras corre
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { vm.tareaLarga() },
                        enabled = !corriendo
                    ) {
                        Text("Iniciar")
                    }
                    OutlinedButton(
                        onClick = { vm.cancelar() },
                        enabled = corriendo
                    ) {
                        Text("Cancelar")
                    }
                }
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${(progreso * 100).toInt()} %",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(text = mensaje)
            }
            Tarjeta("Contar números primos") {
                val calculando = primos is PrimosUi.Calculando
                OutlinedTextField(
                    value = textoN,
                    onValueChange = { textoN = it },
                    label = { Text("Hasta el número") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            // Ignora textos no numéricos o menores que 2
                            val n = textoN.trim().toIntOrNull()
                            if (n != null && n >= 2) {
                                ultimoN = n
                                vm.contar(n)
                            }
                        },
                        enabled = !calculando
                    ) {
                        Text("Contar")
                    }
                    OutlinedButton(
                        onClick = { vm.cancelarPrimos() },
                        enabled = calculando
                    ) {
                        Text("Cancelar")
                    }
                }
                when (val estado = primos) {
                    PrimosUi.Inicial -> {}
                    PrimosUi.Calculando -> Text("Calculando...")
                    PrimosUi.Cancelado -> Text("Cancelado")
                    is PrimosUi.Listo -> RecuadroVerde {
                        Text(
                            text = "Primos hasta $ultimoN: ${estado.cantidad}",
                            fontWeight = FontWeight.Bold,
                            color = VerdeTexto
                        )
                        Text(text = "Tiempo: ${estado.milisegundos} ms", color = VerdeTexto)
                    }
                }
            }
            Tarjeta("Dos cargas") {
                val cargando = carga is CargaUi.Cargando
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = simularError,
                        onCheckedChange = { vm.cambiarSimularError(it) }
                    )
                    Text("Simular error")
                }
                // Mientras carga, los dos botones se deshabilitan
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { vm.cargarUnaPorUna() },
                        enabled = !cargando
                    ) {
                        Text("Una por una")
                    }
                    Button(
                        onClick = { vm.cargarALaVez() },
                        enabled = !cargando
                    ) {
                        Text("A la vez")
                    }
                }
                when (val estado = carga) {
                    CargaUi.Inicial -> {}
                    CargaUi.Cargando -> CircularProgressIndicator()
                    is CargaUi.Exito -> RecuadroVerde {
                        Text(
                            text = "Modo: ${estado.modo}",
                            fontWeight = FontWeight.Bold,
                            color = VerdeTexto
                        )
                        Text(text = "Perfil: ${estado.perfil}", color = VerdeTexto)
                        Text(text = "Pedidos: ${estado.pedidos}", color = VerdeTexto)
                        Text(text = "Tiempo: ${estado.milisegundos} ms", color = VerdeTexto)
                    }
                    is CargaUi.Error -> {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = estado.mensaje,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        // Reintentar vuelve a hacer la carga "A la vez"
                        OutlinedButton(onClick = { vm.cargarALaVez() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

/** Recuadro verde para mostrar un resultado. */
@Composable
private fun RecuadroVerde(contenido: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = VerdeFondo,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = contenido
        )
    }
}

/** Card con el título en azul; el contenido va centrado debajo. */
@Composable
private fun Tarjeta(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
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
