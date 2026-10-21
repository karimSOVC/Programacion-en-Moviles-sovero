package com.sovero.ayudacampus.ui.soporte

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sovero.ayudacampus.data.local.entity.Categoria
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.components.AyudaButton
import com.sovero.ayudacampus.ui.components.AyudaTextField
import com.sovero.ayudacampus.ui.components.iconoCategoria
import com.sovero.ayudacampus.ui.theme.ColorAlta
import com.sovero.ayudacampus.viewmodel.CategoriasViewModel

// Sirve para crear (categoriaId = null) y para editar (categoriaId con valor)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormularioCategoriaScreen(
    vm: CategoriasViewModel,
    categoriaId: Int?,
    onListo: () -> Unit
) {
    val categorias by vm.categorias.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val listo by vm.listo.collectAsState()

    val iconos = listOf("pc", "code", "wifi", "key", "printer", "tag")
    val existente = categorias.firstOrNull { it.id == categoriaId }

    var nombre by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("tag") }
    var activa by remember { mutableStateOf(true) }
    var cargado by remember { mutableStateOf(false) }

    // Al editar, llena el formulario una sola vez con los datos guardados
    LaunchedEffect(existente) {
        if (existente != null && !cargado) {
            nombre = existente.nombre
            icono = existente.icono
            activa = existente.activa
            cargado = true
        }
    }

    LaunchedEffect(listo) {
        if (listo) {
            onListo()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = if (categoriaId == null) "Nueva categoría" else "Editar categoría",
                onAtras = onListo
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Nombre (obligatorio)
            AyudaTextField(
                valor = nombre,
                onCambio = {
                    nombre = it
                    vm.limpiarMensaje()
                },
                etiqueta = "Nombre de la categoría",
                error = if (mensaje == "El nombre es obligatorio") mensaje else null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de icono
            Text(text = "Icono", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                iconos.forEach { i ->
                    val elegido = icono == i
                    OutlinedCard(
                        onClick = { icono = i },
                        border = BorderStroke(
                            width = if (elegido) 2.dp else 1.dp,
                            color = if (elegido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.size(width = 96.dp, height = 56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = iconoCategoria(i),
                                contentDescription = i,
                                tint = if (elegido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Switch 'Activa'
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Categoría activa", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Los estudiantes podrán elegirla",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = activa, onCheckedChange = { activa = it })
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vista previa
            Text(text = "Vista previa", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = iconoCategoria(icono),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (nombre.isBlank()) "Nombre de la categoría" else nombre,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (activa) "Activa" else "Inactiva",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Mensaje del ViewModel (por ejemplo, si no se puede eliminar)
            val texto = mensaje
            if (texto != null && texto != "El nombre es obligatorio") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = texto, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            AyudaButton(
                texto = "Guardar categoría",
                onClick = {
                    vm.guardar(
                        Categoria(
                            id = existente?.id ?: 0,
                            nombre = nombre,
                            icono = icono,
                            activa = activa
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onListo,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cancelar")
            }

            // Eliminar solo aparece al editar
            if (existente != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { vm.eliminar(existente) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar categoría", color = ColorAlta)
                }
            }
        }
    }
}
