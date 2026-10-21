package com.sovero.ayudacampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sovero.ayudacampus.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Se declara una sola vez, a nivel de archivo
val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ajustes")

// Preferencias pequenas (clave-valor). Los tickets, comentarios y categorias van en Room.
class PreferenciasStore(private val context: Context) {

    // Claves
    private val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")     // CLIENTE, ADMIN o vacio
    private val USUARIO_ID = intPreferencesKey("usuarioId")
    private val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
    private val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
    private val RECIBIR_TICKETS = booleanPreferencesKey("recibirTickets")

    // Lectura: un Flow por cada preferencia
    val perfilActivo: Flow<String> = context.dataStore.data.map { it[PERFIL_ACTIVO] ?: "" }
    val usuarioId: Flow<Int> = context.dataStore.data.map { it[USUARIO_ID] ?: 0 }
    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { it[MODO_OSCURO] ?: false }
    val notificaciones: Flow<Boolean> = context.dataStore.data.map { it[NOTIFICACIONES] ?: true }
    val recibirTickets: Flow<Boolean> = context.dataStore.data.map { it[RECIBIR_TICKETS] ?: true }

    // Escritura: funciones suspend
    suspend fun guardarSesion(rol: Rol, usuarioId: Int) {
        context.dataStore.edit {
            it[PERFIL_ACTIVO] = rol.name
            it[USUARIO_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(b: Boolean) {
        context.dataStore.edit { it[MODO_OSCURO] = b }
    }

    suspend fun guardarNotificaciones(b: Boolean) {
        context.dataStore.edit { it[NOTIFICACIONES] = b }
    }

    suspend fun guardarRecibirTickets(b: Boolean) {
        context.dataStore.edit { it[RECIBIR_TICKETS] = b }
    }

    // Borra el perfil activo; el modo oscuro y los demas ajustes se conservan
    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it.remove(PERFIL_ACTIVO)
            it.remove(USUARIO_ID)
        }
    }
}
