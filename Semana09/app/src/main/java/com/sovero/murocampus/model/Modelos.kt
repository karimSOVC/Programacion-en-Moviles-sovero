package com.sovero.murocampus.model

import kotlinx.serialization.Serializable

// Los nombres de las propiedades son iguales a los del JSON de la API.
// Si uno no coincide, la app falla con SerializationException.

/** Una publicación: GET posts y GET posts/{id}. */
@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

/** Un comentario de una publicación: GET posts/{id}/comments. */
@Serializable
data class Comentario(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)

/** Lo que enviamos al crear (POST posts): no lleva id, lo pone el servidor. */
@Serializable
data class NuevoPost(
    val userId: Int,
    val title: String,
    val body: String
)

/** Ejemplo resuelto [E]: un usuario de GET users. */
@Serializable
data class Usuario(
    val id: Int,
    val name: String,
    val email: String
)
