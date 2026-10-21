package com.sovero.ayudacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sovero.ayudacampus.data.local.dao.CategoriaDao
import com.sovero.ayudacampus.data.local.dao.TicketDao
import com.sovero.ayudacampus.data.local.dao.UsuarioDao
import com.sovero.ayudacampus.data.local.entity.Categoria
import com.sovero.ayudacampus.data.local.entity.Comentario
import com.sovero.ayudacampus.data.local.entity.Ticket
import com.sovero.ayudacampus.data.local.entity.Usuario

@Database(
    entities = [Categoria::class, Usuario::class, Ticket::class, Comentario::class],
    version = 1,
    exportSchema = false
)
abstract class AyudaDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile
        private var INSTANCE: AyudaDatabase? = null

        // Una sola instancia de la base para toda la app (singleton)
        fun obtener(context: Context): AyudaDatabase = INSTANCE ?: synchronized(this) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AyudaDatabase::class.java,
                "ayudacampus.db"
            ).build()
            INSTANCE = db
            db
        }
    }
}