package com.sovero.murocampus.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/** Mejora (Fase 2): las consultas a la tabla "posts" de Room. */
@Dao
interface PostDao {

    // Una página guardada, en el mismo orden que la API
    @Query("SELECT * FROM posts ORDER BY id LIMIT :limite OFFSET :inicio")
    suspend fun pagina(inicio: Int, limite: Int): List<PostEntity>

    @Query("SELECT * FROM posts ORDER BY id")
    suspend fun todas(): List<PostEntity>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY id")
    suspend fun porUsuario(userId: Int): List<PostEntity>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun buscar(id: Int): PostEntity?

    // Inserta las nuevas y actualiza las que ya estaban (mismo id)
    @Upsert
    suspend fun guardar(posts: List<PostEntity>)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun borrar(id: Int)

    @Query("UPDATE posts SET title = :titulo WHERE id = :id")
    suspend fun cambiarTitulo(id: Int, titulo: String)
}
