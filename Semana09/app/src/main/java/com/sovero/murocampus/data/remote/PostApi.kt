package com.sovero.murocampus.data.remote

import com.sovero.murocampus.model.Comentario
import com.sovero.murocampus.model.NuevoPost
import com.sovero.murocampus.model.Post
import com.sovero.murocampus.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Las llamadas al servicio JSONPlaceholder. Solo describe cada llamada
 * con anotaciones; el código lo genera Retrofit.
 *
 * Las rutas no empiezan con "/": la barra reemplazaría el camino de la
 * URL base.
 */
interface PostApi {

    // GET posts → las 100 publicaciones
    @GET("posts")
    suspend fun listar(): List<Post>

    // GET posts/1 → @Path pone el id dentro de la ruta
    @GET("posts/{id}")
    suspend fun detalle(@Path("id") id: Int): Post

    // GET posts/1/comments → los comentarios de esa publicación
    @GET("posts/{id}/comments")
    suspend fun comentarios(@Path("id") id: Int): List<Comentario>

    // GET posts?userId=2 → @Query agrega "?userId=2" al final de la URL
    @GET("posts")
    suspend fun porUsuario(@Query("userId") userId: Int): List<Post>

    // Mejora (Fase 2): GET posts?_start=0&_limit=20 → una página de 20
    @GET("posts")
    suspend fun pagina(
        @Query("_start") inicio: Int,
        @Query("_limit") limite: Int
    ): List<Post>

    // Mejora (Fase 2): DELETE posts/1 → respuesta vacía. Response<Unit> deja
    // ver el código HTTP (200 si se borró) aunque no haya cuerpo
    @DELETE("posts/{id}")
    suspend fun borrar(@Path("id") id: Int): Response<Unit>

    // POST posts → @Body manda el objeto como JSON en el cuerpo
    @POST("posts")
    suspend fun crear(@Body nuevo: NuevoPost): Post
}

/** Ejemplo resuelto [E]: la API de usuarios. */
interface UsuarioApi {
    @GET("users")
    suspend fun listar(): List<Usuario>
}
