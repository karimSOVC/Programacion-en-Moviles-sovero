package com.sovero.ayudacampus.data.local

import com.sovero.ayudacampus.data.local.entity.Categoria

// Carga los datos de prueba solo la primera vez que se abre la app
suspend fun sembrar(db: AyudaDatabase) {
    // TODO (Paso 3): cambiar la condicion a usuarioDao().contar() == 0
    if (db.categoriaDao().contar() == 0) {
        db.categoriaDao().insertarTodas(
            listOf(
                Categoria(nombre = "Hardware", icono = "pc"),
                Categoria(nombre = "Software", icono = "code"),
                Categoria(nombre = "Red / Wi-Fi", icono = "wifi"),
                Categoria(nombre = "Cuenta", icono = "key"),
                Categoria(nombre = "Impresión", icono = "printer")
            )
        )

        // TODO (Paso 3): insertar los 2 usuarios (estudiante y soporte)
        // TODO (Paso 3): insertar los 6 tickets de ejemplo con 3 o 4 comentarios
    }
}
