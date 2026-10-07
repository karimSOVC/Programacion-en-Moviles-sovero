package com.tecsup.mibodega.ui.cliente.screens.pedidos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.mibodega.ui.cliente.Rutas
import com.tecsup.mibodega.ui.cliente.modelo.ItemCarrito
import com.tecsup.mibodega.ui.cliente.modelo.Pedido
import com.tecsup.mibodega.ui.cliente.modelo.listaProductosFake
import com.tecsup.mibodega.ui.componentes.BarraInferior
import com.tecsup.mibodega.ui.theme.BodegaTheme
import com.tecsup.mibodega.ui.theme.GrisClaro
import com.tecsup.mibodega.ui.theme.VerdeBodega

/**
 * Destino "Pedidos" del menú inferior.
 * Muestra la lista de pedidos confirmados por el usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    pedidos: List<Pedido>,
    onNavegar: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis pedidos", fontWeight = FontWeight.Bold) })
        },
        bottomBar = { BarraInferior(rutaActual = Rutas.PEDIDOS, onNavegar = onNavegar) }
    ) { paddingInterno ->
        if (pedidos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = VerdeBodega,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Aún no tienes pedidos",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Cuando confirmes una compra aparecerá aquí.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingInterno),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos, key = { it.id }) { pedido ->
                    PedidoCard(pedido = pedido)
                }
            }
        }
    }
}

@Composable
private fun PedidoCard(pedido: Pedido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GrisClaro),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedido #${pedido.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "S/ %.2f".format(pedido.total),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = VerdeBodega
                )
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Spacer(Modifier.height(4.dp))

            Text(
                text = "Productos:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            pedido.items.forEach { item ->
                Text(
                    text = "${item.cantidad} x ${item.producto.nombre}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Dirección: ${pedido.direccion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Método de pago: ${pedido.metodoPago}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PedidosPreview() {
    val pedidosEjemplo = listOf(
        Pedido(
            id = 1,
            items = listOf(
                ItemCarrito(listaProductosFake[0], 2),
                ItemCarrito(listaProductosFake[2], 1)
            ),
            total = 18.50,
            direccion = "Av. Los Olivos 123",
            metodoPago = "Yape"
        )
    )
    BodegaTheme {
        PedidosScreen(pedidos = pedidosEjemplo, onNavegar = {})
    }
}
