package com.sovero.ayudacampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sovero.ayudacampus.model.Prioridad
import com.sovero.ayudacampus.ui.components.AppTopBar
import com.sovero.ayudacampus.ui.components.AyudaButton
import com.sovero.ayudacampus.ui.components.AyudaTextField
import com.sovero.ayudacampus.ui.components.iconoCategoria
import com.sovero.ayudacampus.ui.theme.colorPrioridad
import com.sovero.ayudacampus.viewmodel.NuevoTicketViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NuevoTicketScreen(
    vm: NuevoTicketViewModel,
    onAtras: () -> Unit,
    onEnviado: () -> Unit
) {
    val categorias by vm.categorias.collectAsState()
    val categoriaId by vm.categoriaId.collectAsState()
    val titulo by vm.titulo.collectAsState()
    val ubicacion by vm.ubicacion.collectAsState()
    val prioridad by vm.prioridad.collectAsState()
    val descripcion by vm.descripcion.collectAsState()
    val errorCategoria by vm.errorCategoria.collectAsState()
    val errorTitulo by vm.errorTitulo.collectAsState()
    val errorDescripcion by vm.errorDescripcion.collectAsState()
    val enviado by vm.enviado.collectAsState()

    // Cuando el ticket se guarda, volvemos a Mis tickets
    LaunchedEffect(enviado) {
        if (enviado) {
            onEnviado()
        }
    }

    Scaffold(
        topBar = { AppTopBar(titulo = "Nuevo ticket", onAtras = onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Categoria
            Text(
                text = "Categoría",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categorias.forEach { c ->
                    FilterChip(
                        selected = categoriaId == c.id,
                        onClick = { vm.elegirCategoria(c.id) },
                        label = { Text(c.nombre) },
                        leadingIcon = {
                            Icon(
                                imageVector = iconoCategoria(c.icono),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
            val mensajeCategoria = errorCategoria
            if (mensajeCategoria != null) {
                Text(
                    text = mensajeCategoria,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AyudaTextField(
                valor = titulo,
                onCambio = { vm.cambiarTitulo(it) },
                etiqueta = "Título",
                error = errorTitulo
            )
            AyudaTextField(
                valor = ubicacion,
                onCambio = { vm.cambiarUbicacion(it) },
                etiqueta = "Ubicación"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Prioridad: Baja, Media o Alta (Media por defecto)
            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                Prioridad.entries.forEachIndexed { i, p ->
                    SegmentedButton(
                        selected = prioridad == p,
                        onClick = { vm.cambiarPrioridad(p) },
                        shape = SegmentedButtonDefaults.itemShape(index = i, count = Prioridad.entries.size),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = colorPrioridad(p),
                            activeContentColor = Color.White
                        )
                    ) {
                        Text(p.etiqueta)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AyudaTextField(
                valor = descripcion,
                onCambio = { vm.cambiarDescripcion(it) },
                etiqueta = "Descripción",
                error = errorDescripcion,
                lineas = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            AyudaButton(
                texto = "Enviar ticket",
                onClick = { vm.enviar() }
            )
        }
    }
}
