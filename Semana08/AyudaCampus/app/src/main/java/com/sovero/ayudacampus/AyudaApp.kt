package com.sovero.ayudacampus

import android.app.Application
import com.sovero.ayudacampus.data.local.AyudaDatabase
import com.sovero.ayudacampus.data.local.sembrar
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.data.repository.AyudaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AyudaApp : Application() {

    lateinit var repository: AyudaRepository

    override fun onCreate() {
        super.onCreate()
        val db = AyudaDatabase.obtener(this)
        repository = AyudaRepositoryRoom(db)

        // Carga los datos iniciales en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            sembrar(db)
        }
    }
}
