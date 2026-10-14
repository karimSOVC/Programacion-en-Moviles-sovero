package com.sovero.ayudacampus.data.repository

import com.sovero.ayudacampus.data.local.AyudaDatabase
import com.sovero.ayudacampus.data.local.entity.Categoria
import kotlinx.coroutines.flow.Flow

class AyudaRepositoryRoom(private val db: AyudaDatabase) : AyudaRepository {

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

    // TODO (Paso 4): login
    // TODO (Paso 4): crearTicket, observarTicketsCliente, observarTodosLosTickets, observarTicket
    // TODO (Paso 4): cambiarEstado, cambiarPrioridad, cerrarConCalificacion, reabrir, agregarComentario
    // TODO (Paso 4): observarCantidadPorEstado, observarCantidadPorCategoria, observarAltasSinAtender
}
