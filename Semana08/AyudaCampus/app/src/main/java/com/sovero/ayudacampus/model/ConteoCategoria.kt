package com.sovero.ayudacampus.model

// Resultado del COUNT por categoria (lo usa el panel de soporte)
data class ConteoCategoria(
    val nombre: String,
    val cantidad: Int
)
