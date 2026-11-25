package com.sovero.murocampus.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sovero.murocampus.model.Post

/**
 * Mejora (Fase 2): una fila de la tabla "posts" de Room. Guarda las
 * publicaciones descargadas para poder mostrarlas sin internet.
 */
@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

/** Convierte el modelo de la API en una fila de la base de datos. */
fun Post.aEntidad() = PostEntity(id = id, userId = userId, title = title, body = body)

/** Convierte una fila de la base de datos en el modelo que usa la app. */
fun PostEntity.aPost() = Post(userId = userId, id = id, title = title, body = body)
