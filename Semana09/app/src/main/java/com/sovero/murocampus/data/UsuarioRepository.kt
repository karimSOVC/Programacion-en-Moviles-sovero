package com.sovero.murocampus.data

import com.sovero.murocampus.data.remote.UsuarioApi
import com.sovero.murocampus.model.Usuario
import kotlinx.coroutines.CancellationException

/** Ejemplo resuelto [E]: el mismo patrón que PostRepository, para Usuario. */
interface UsuarioRepository {
    suspend fun listar(): Result<List<Usuario>>
}

class UsuarioRepositoryRemote(private val api: UsuarioApi) : UsuarioRepository {
    override suspend fun listar(): Result<List<Usuario>> =
        try {
            Result.success(api.listar())
        } catch (e: CancellationException) {
            throw e // nunca se traga la cancelación
        } catch (e: Exception) {
            Result.failure(e)
        }
}
