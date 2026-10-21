package com.sovero.ayudacampus.data.local

import com.sovero.ayudacampus.data.local.entity.Categoria
import com.sovero.ayudacampus.data.local.entity.Comentario
import com.sovero.ayudacampus.data.local.entity.Ticket
import com.sovero.ayudacampus.data.local.entity.Usuario
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.model.Prioridad
import com.sovero.ayudacampus.model.Rol

// Carga los datos de prueba solo la primera vez que se abre la app
suspend fun sembrar(db: AyudaDatabase) {
    if (db.usuarioDao().contar() == 0) {
        val categoriaDao = db.categoriaDao()
        val usuarioDao = db.usuarioDao()
        val ticketDao = db.ticketDao()

        // 5 categorias (quedan con id 1 al 5 en este orden)
        categoriaDao.insertarTodas(
            listOf(
                Categoria(nombre = "Hardware", icono = "pc"),
                Categoria(nombre = "Software", icono = "code"),
                Categoria(nombre = "Red / Wi-Fi", icono = "wifi"),
                Categoria(nombre = "Cuenta", icono = "key"),
                Categoria(nombre = "Impresión", icono = "printer")
            )
        )

        // 2 usuarios: id 1 = estudiante, id 2 = soporte
        usuarioDao.insertarTodos(
            listOf(
                Usuario(nombre = "Juan Pérez", correo = "karim@campus.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Soporte TI", correo = "soporte@campus.test", clave = "admin", rol = Rol.ADMIN.name)
            )
        )

        val hora = 3600 * 1000L
        val ahora = System.currentTimeMillis()

        // 6 tickets de ejemplo en distintos estados y prioridades
        ticketDao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 3,
                titulo = "No hay Wi-Fi en el aula B-201",
                descripcion = "La red del campus no aparece en mi laptop desde la mañana.",
                ubicacion = "Pabellón B, aula 201",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 3 * hora
            )
        )
        val t2 = ticketDao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 1,
                titulo = "La PC del laboratorio no enciende",
                descripcion = "La computadora 12 del laboratorio 3 no prende al presionar el botón.",
                ubicacion = "Laboratorio 3, PC 12",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.EN_PROCESO.name,
                fecha = ahora - 5 * hora
            )
        )
        val t3 = ticketDao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 4,
                titulo = "Mi cuenta del campus está bloqueada",
                descripcion = "Puse mal la contraseña varias veces y ahora no puedo ingresar.",
                ubicacion = "Remoto",
                prioridad = Prioridad.ALTA.name, estado = EstadoTicket.RESUELTO.name,
                fecha = ahora - 26 * hora
            )
        )
        val t4 = ticketDao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 5,
                titulo = "La impresora de biblioteca imprime manchado",
                descripcion = "Las hojas salen con manchas negras en el borde.",
                ubicacion = "Biblioteca, primer piso",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.CERRADO.name,
                fecha = ahora - 72 * hora,
                fechaCierre = ahora - 48 * hora,
                calificacion = 5
            )
        )
        ticketDao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 2,
                titulo = "No puedo instalar Android Studio",
                descripcion = "El instalador se detiene a la mitad y muestra un error.",
                ubicacion = "Laboratorio 5, PC 4",
                prioridad = Prioridad.MEDIA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - hora / 2
            )
        )
        ticketDao.insertarTicket(
            Ticket(
                clienteId = 1, categoriaId = 1,
                titulo = "El proyector del aula A-105 parpadea",
                descripcion = "La imagen se apaga y se prende cada pocos segundos.",
                ubicacion = "Pabellón A, aula 105",
                prioridad = Prioridad.BAJA.name, estado = EstadoTicket.ABIERTO.name,
                fecha = ahora - 2 * hora
            )
        )

        // 4 comentarios
        ticketDao.insertarComentario(
            Comentario(
                ticketId = t2.toInt(), autorId = 2, autorNombre = "Soporte TI", autorRol = Rol.ADMIN.name,
                texto = "Ya revisamos la PC, hay que cambiar la fuente de poder.",
                fecha = ahora - 4 * hora
            )
        )
        ticketDao.insertarComentario(
            Comentario(
                ticketId = t2.toInt(), autorId = 1, autorNombre = "Juan Pérez", autorRol = Rol.CLIENTE.name,
                texto = "Gracias, quedo atento.",
                fecha = ahora - 3 * hora
            )
        )
        ticketDao.insertarComentario(
            Comentario(
                ticketId = t3.toInt(), autorId = 2, autorNombre = "Soporte TI", autorRol = Rol.ADMIN.name,
                texto = "Tu cuenta ya fue desbloqueada, prueba ingresar de nuevo.",
                fecha = ahora - 20 * hora
            )
        )
        ticketDao.insertarComentario(
            Comentario(
                ticketId = t4.toInt(), autorId = 2, autorNombre = "Soporte TI", autorRol = Rol.ADMIN.name,
                texto = "Se cambió el tóner de la impresora.",
                fecha = ahora - 50 * hora
            )
        )
    }
}