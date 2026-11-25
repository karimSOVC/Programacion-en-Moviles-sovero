package com.sovero.murocampus.data

import com.sovero.murocampus.data.remote.PostApi
import com.sovero.murocampus.model.CambioTitulo
import com.sovero.murocampus.model.Comentario
import com.sovero.murocampus.model.NuevoPost
import com.sovero.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

/**
 * Lo que el ViewModel puede pedir. Cada función devuelve un Result:
 * éxito con los datos o fallo con el error, nunca una excepción suelta.
 */
interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>

    // Mejora (Fase 2): una página de publicaciones, desde [inicio]
    suspend fun pagina(inicio: Int, limite: Int): Result<List<Post>>

    // Mejora (Fase 2): borra la publicación; si sale bien, devuelve el código HTTP
    suspend fun borrar(id: Int): Result<Int>

    // Mejora (Fase 2): cambia el título con PATCH y devuelve la respuesta
    suspend fun editarTitulo(id: Int, titulo: String): Result<Post>
}

/** Implementación que pide los datos a internet con Retrofit. */
class PostRepositoryRemote(private val api: PostApi) : PostRepository {

    override suspend fun listar(): Result<List<Post>> = llamar { api.listar() }

    override suspend fun detalle(id: Int): Result<Post> = llamar { api.detalle(id) }

    override suspend fun comentarios(id: Int): Result<List<Comentario>> =
        llamar { api.comentarios(id) }

    override suspend fun porUsuario(userId: Int): Result<List<Post>> =
        llamar { api.porUsuario(userId) }

    override suspend fun crear(nuevo: NuevoPost): Result<Post> = llamar { api.crear(nuevo) }

    override suspend fun pagina(inicio: Int, limite: Int): Result<List<Post>> =
        llamar { api.pagina(inicio, limite) }

    override suspend fun borrar(id: Int): Result<Int> = llamar {
        val respuesta = api.borrar(id)
        // Response no lanza error con 404 o 500: hay que revisar el código
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
        respuesta.code()
    }

    override suspend fun editarTitulo(id: Int, titulo: String): Result<Post> =
        llamar { api.editarTitulo(id, CambioTitulo(title = titulo)) }

    /**
     * Hace la llamada y la envuelve en un Result, así no se repite el
     * try/catch en las cinco funciones.
     */
    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            // Nunca se traga la cancelación: se vuelve a lanzar
            throw e
        } catch (e: Exception) {
            // Sin internet (IOException), error del servidor (HttpException)
            // o JSON inesperado (SerializationException)
            Result.failure(e)
        }
}
