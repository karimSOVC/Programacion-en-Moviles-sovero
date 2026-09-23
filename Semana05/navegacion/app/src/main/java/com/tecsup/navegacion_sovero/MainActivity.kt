package com.tecsup.navegacion_sovero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.navegacion_sovero.navigation.AppNavigation
import com.tecsup.navegacion_sovero.ui.theme.Navegacion_soveroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navegacion_soveroTheme {
                AppNavigation()
            }
        }
    }
}