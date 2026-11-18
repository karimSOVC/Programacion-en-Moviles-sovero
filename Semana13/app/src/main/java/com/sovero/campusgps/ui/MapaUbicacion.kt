package com.sovero.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.sovero.campusgps.model.LugarConDistancia
import com.sovero.campusgps.model.UbicacionUi

/**
 * Mapa de Google con la posición del usuario.
 *
 * Solo dibuja lo que recibe: el estado, la lista de lugares y el
 * lugar seleccionado. No consulta ningún Repository.
 */
@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier,
    masCercanoId: Int? = null,
    radioMetros: Int? = null
) {
    // Cámara inicial: Lima, zoom 12
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0464, -77.0428), 12f)
    }

    // Cuando llega la ubicación, la cámara viaja hasta ella (zoom 16)
    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            val destino = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            camara.animate(CameraUpdateFactory.newLatLngZoom(destino, 16f))
        }
    }

    // Cuando se toca un lugar en la lista, la cámara viaja hasta él (zoom 17)
    LaunchedEffect(seleccionado) {
        val lugar = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar
        if (lugar != null) {
            val destino = LatLng(lugar.latitud, lugar.longitud)
            camara.animate(CameraUpdateFactory.newLatLngZoom(destino, 17f))
        }
    }

    GoogleMap(modifier = modifier, cameraPositionState = camara) {
        // Marcador rojo (el de por defecto) con mi posición
        if (estado is UbicacionUi.Lista) {
            Marker(
                state = rememberUpdatedMarkerState(
                    position = LatLng(estado.posicion.latitud, estado.posicion.longitud)
                ),
                title = "Estás aquí"
            )
            // Mejora (Fase 2): círculo del radio de cercanía alrededor de mí
            if (radioMetros != null) {
                Circle(
                    center = LatLng(estado.posicion.latitud, estado.posicion.longitud),
                    radius = radioMetros.toDouble(),
                    strokeColor = Color(0xFF1565C0),
                    strokeWidth = 4f,
                    fillColor = Color(0x331565C0)
                )
            }
        }
        // Un marcador azul por cada lugar del campus (nombre y categoría)
        lugares.forEach { item ->
            key(item.lugar.id) {
                val estadoMarcador = rememberUpdatedMarkerState(
                    position = LatLng(item.lugar.latitud, item.lugar.longitud)
                )
                // Si es el lugar seleccionado, se muestra su nombre en el mapa
                LaunchedEffect(seleccionado) {
                    if (seleccionado == item.lugar.id) estadoMarcador.showInfoWindow()
                }
                Marker(
                    state = estadoMarcador,
                    title = item.lugar.nombre,
                    snippet = item.lugar.categoria,
                    // El más cercano en verde; los demás en azul
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (item.lugar.id == masCercanoId) BitmapDescriptorFactory.HUE_GREEN
                        else BitmapDescriptorFactory.HUE_AZURE
                    )
                )
            }
        }
    }
}
