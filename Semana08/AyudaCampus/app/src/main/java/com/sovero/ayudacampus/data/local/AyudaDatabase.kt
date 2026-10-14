package com.sovero.ayudacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sovero.ayudacampus.data.local.dao.CategoriaDao
import com.sovero.ayudacampus.data.local.entity.Categoria

@Database(
    entities = [Categoria::class /* TODO (Paso 3): agrega Usuario, Ticket y Comentario */],
    version = 1,
    exportSchema = false
)
abstract class AyudaDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao

    // TODO (Paso 3): usuarioDao() y ticketDao()

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
