package com.tecsup.mibodega.ui.cliente.screens.entrega

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.mibodega.ui.componentes.BotonPrimario
import com.tecsup.mibodega.ui.componentes.CampoTexto
import com.tecsup.mibodega.ui.theme.BodegaTheme
import com.tecsup.mibodega.ui.theme.GrisClaro
import com.tecsup.mibodega.ui.theme.VerdeBodega

private val metodosPago = listOf("Efectivo", "Yape", "Plin")

/**
 * Pantalla 6: Datos de entrega (mockup "Cliente").
 * Guarda su propio estado de formulario (remember), igual que Registro.
 * Al confirmar entrega los datos ya listos; la navegación la decide ClienteApp.
 *
 * @param total lo que se va a pagar (subtotal + delivery), viene del carrito
 */
@Composable
fun DatosEntregaScreen(
    total: Double,
    onVolver: () -> Unit,
    onConfirmarPedido: (direccion: String, metodoPago: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf(metodosPago.first()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        EncabezadoEntrega(onVolver = onVolver)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            CampoTexto(
                etiqueta = "¿Quién recibe?",
                valor = nombre,
                onValorCambia = { nombre = it },
                placeholder = "Juan Pérez"
            )
            Spacer(Modifier.height(16.dp))

            CampoTexto(
                etiqueta = "Teléfono de contacto",
                valor = telefono,
                onValorCambia = { telefono = it },
                placeholder = "987 654 321",
                teclado = KeyboardType.Phone
            )
            Spacer(Modifier.height(16.dp))

            CampoTexto(
                etiqueta = "Dirección de entrega",
                valor = direccion,
                onValorCambia = { direccion = it },
                placeholder = "Av. Los Olivos 123"
            )
            Spacer(Modifier.height(16.dp))

            CampoTexto(
                etiqueta = "Referencia",
                valor = referencia,
                onValorCambia = { referencia = it },
                placeholder = "Frente al parque"
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Método de pago",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                metodosPago.forEach { metodo ->
                    ChipPago(
                        texto = metodo,
                        seleccionado = metodo == metodoPago,
                        onClick = { metodoPago = metodo }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
            HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total a pagar", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "S/ %.2f".format(total),
                    style = MaterialTheme.typography.titleMedium,
                    color = VerdeBodega
                )
            }

            Spacer(Modifier.height(16.dp))

            // La referencia es opcional; los otros tres datos son obligatorios.
            BotonPrimario(
                texto = "Confirmar pedido",
                onClick = { onConfirmarPedido(direccion, metodoPago) },
                habilitado = nombre.isNotBlank() && telefono.isNotBlank() && direccion.isNotBlank()
            )
        }
    }
}

// Sub-composables PRIVADOS: solo los usa esta pantalla.

@Composable
private fun EncabezadoEntrega(onVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
        }
        Text(
            text = "Datos de entrega",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ChipPago(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val fondo = if (seleccionado) VerdeBodega else GrisClaro
    val contenido = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(fondo)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = texto, color = contenido, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DatosEntregaPreview() {
    BodegaTheme {
        DatosEntregaScreen(total = 19.50, onVolver = {}, onConfirmarPedido = { _, _ -> })
    }
}
