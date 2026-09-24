package com.tecsup.tecsupfit

sealed class Screen(val route: String, val titulo: String) {
    object Inicio : Screen("inicio", "Inicio")
    object Reservas : Screen("reservas", "Mis reservas")
    object Rutinas : Screen("rutinas", "Rutinas")
    object Perfil : Screen("perfil", "Mi perfil")

    object Detalle : Screen("detalle/{claseId}", "Detalle de clase") {
        fun crearRuta(claseId: Int) = "detalle/$claseId"
    }

    object Confirmacion : Screen("confirmacion/{claseId}/{horarioIndex}", "Confirmación") {
        fun crearRuta(claseId: Int, horarioIndex: Int) = "confirmacion/$claseId/$horarioIndex"
    }
}
