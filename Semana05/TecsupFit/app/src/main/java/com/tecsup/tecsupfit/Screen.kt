package com.tecsup.tecsupfit

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")

    object Detalle : Screen("detalle/{claseId}") {
        fun crearRuta(claseId: Int) = "detalle/$claseId"
    }

    object Confirmacion : Screen("confirmacion/{claseId}/{horarioIndex}") {
        fun crearRuta(claseId: Int, horarioIndex: Int) = "confirmacion/$claseId/$horarioIndex"
    }
}
