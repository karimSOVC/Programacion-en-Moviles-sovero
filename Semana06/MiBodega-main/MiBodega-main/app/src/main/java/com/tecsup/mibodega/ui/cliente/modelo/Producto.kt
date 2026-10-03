package com.tecsup.mibodega.ui.cliente.modelo

import androidx.annotation.DrawableRes

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val categoria: String,
    // Foto del producto: un recurso de res/drawable (ej. R.drawable.coca_cola)
    @DrawableRes val imagen: Int
)
