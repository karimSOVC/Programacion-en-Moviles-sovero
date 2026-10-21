package com.sovero.ayudacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sovero.ayudacampus.model.Rol
import com.sovero.ayudacampus.ui.navigation.NavAyuda
import com.sovero.ayudacampus.ui.theme.AyudaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = (application as AyudaApp).repository

        setContent {
            // Tema base; cada pantalla aplica su rol y el modo oscuro (DataStore) en NavAyuda
            AyudaTheme(rol = Rol.ADMIN) {
                NavAyuda(repo)
            }
        }
    }
}
