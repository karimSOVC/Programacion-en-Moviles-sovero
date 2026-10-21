package com.sovero.ayudacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sovero.ayudacampus.data.local.entity.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Insert
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Usuario?

    // Primer usuario con ese rol (para cambiar de perfil en modo práctica)
    @Query("SELECT * FROM usuarios WHERE rol = :rol LIMIT 1")
    suspend fun obtenerPorRol(rol: String): Usuario?
}