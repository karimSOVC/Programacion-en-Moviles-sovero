package com.sovero.campusgps.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sovero.campusgps.model.TipoError
import com.sovero.campusgps.model.UbicacionUi

/**
 * Recuadro de error con el mensaje y, debajo, el botón que corresponde
 * según el tipo de problema.
 */
@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onAbrirAjustes: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = error.mensaje,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (error.tipo) {
                // Permiso negado: llevar a los ajustes de la app
                TipoError.SIN_PERMISO -> Button(onClick = onAbrirAjustes) {
                    Text("Abrir ajustes")
                }
                // GPS apagado u otro problema: volver a intentar
                TipoError.GPS_APAGADO, TipoError.OTRO -> Button(onClick = onReintentar) {
                    Text("Reintentar")
                }
                // Sin Google Play Services: no hay botón, no se puede arreglar desde la app
                TipoError.SIN_PLAY_SERVICES -> Unit
            }
        }
    }
}
