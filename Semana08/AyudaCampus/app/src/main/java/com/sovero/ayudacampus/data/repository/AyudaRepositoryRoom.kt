package com.sovero.ayudacampus.data.repository

import com.sovero.ayudacampus.data.local.AyudaDatabase
import com.sovero.ayudacampus.data.local.entity.Categoria
import com.sovero.ayudacampus.data.local.entity.Comentario
import com.sovero.ayudacampus.data.local.entity.Ticket
import com.sovero.ayudacampus.data.local.entity.Usuario
import com.sovero.ayudacampus.data.local.relation.TicketConDetalle
import com.sovero.ayudacampus.model.ConteoCategoria
import com.sovero.ayudacampus.model.EstadoTicket
import com.sovero.ayudacampus.model.Prioridad
import com.sovero.ayudacampus.model.Rol
import kotlinx.coroutines.flow.Flow

class AyudaRepositoryRoom(private val db: AyudaDatabase) : AyudaRepository {

    // Usuarios
    override suspend fun login(correo: String, clave: String): Usuario? =
        db.usuarioDao().login(correo, clave)

    override suspend fun usuarioPorId(id: Int): Usuario? =
        db.usuarioDao().obtenerPorId(id)

    override suspend fun usuarioPorRol(rol: Rol): Usuario? =
        db.usuarioDao().obtenerPorRol(rol.name)

    // Categorías (ejemplo resuelto)
    override fun observarCategorias(): Flow<List<Categoria>> =
        db.categoriaDao().observarTodas()

    override fun observarCategoriasActivas(): Flow<List<Categoria>> =
        db.categoriaDao().observarActivas()

    override suspend fun guardarCategoria(c: Categoria) {
        db.categoriaDao().insertar(c)
    }

    override suspend fun eliminarCategoria(c: Categoria) {
        db.categoriaDao().eliminar(c)
    }

    override suspend fun cambiarActiva(id: Int, activa: Boolean) {
        db.categoriaDao().cambiarActiva(id, activa)
    }

    // Tickets
    override suspend fun crearTicket(
        clienteId: Int,
        categoriaId: Int,
        titulo: String,
        descripcion: String,
        ubicacion: String,
        prioridad: Prioridad
    ): Long {
        val ticket = Ticket(
            clienteId = clienteId,
            categoriaId = categoriaId,
            titulo = titulo,
            descripcion = descripcion,
            ubicacion = ubicacion,
            prioridad = prioridad.name,
            estado = EstadoTicket.ABIERTO.name,
            fecha = System.currentTimeMillis()
        )
        return db.ticketDao().insertarTicket(ticket)
    }

    override fun observarTicketsCliente(clienteId: Int): Flow<List<TicketConDetalle>> =
        db.ticketDao().observarPorCliente(clienteId)

    override fun observarTodosLosTickets(): Flow<List<TicketConDetalle>> =
        db.ticketDao().observarTodos()

    override fun observarTicket(id: Int): Flow<TicketConDetalle?> =
        db.ticketDao().observarUno(id)

    override suspend fun cambiarEstado(ticketId: Int, estado: EstadoTicket) {
        db.ticketDao().actualizarEstado(ticketId, estado.name)
    }

    override suspend fun cambiarPrioridad(ticketId: Int, prioridad: Prioridad) {
        db.ticketDao().actualizarPrioridad(ticketId, prioridad.name)
    }

    override suspend fun cerrarConCalificacion(ticketId: Int, calificacion: Int) {
        db.ticketDao().cerrar(ticketId, calificacion, System.currentTimeMillis())
    }

    override suspend fun reabrir(ticketId: Int) {
        db.ticketDao().reabrir(ticketId)
    }

    override suspend fun agregarComentario(ticketId: Int, autor: Usuario, texto: String) {
        val comentario = Comentario(
            ticketId = ticketId,
            autorId = autor.id,
            autorNombre = autor.nombre,
            autorRol = autor.rol,
            texto = texto,
            fecha = System.currentTimeMillis()
        )
        db.ticketDao().insertarComentario(comentario)
    }

    // Panel
    override fun observarCantidadPorEstado(estado: EstadoTicket): Flow<Int> =
        db.ticketDao().contarPorEstado(estado.name)

    override fun observarCantidadPorCategoria(): Flow<List<ConteoCategoria>> =
        db.ticketDao().contarPorCategoria()

    override fun observarAltasSinAtender(limite: Long): Flow<Int> =
        db.ticketDao().contarAltasSinAtender(limite)
}
