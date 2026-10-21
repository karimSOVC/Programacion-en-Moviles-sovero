package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.local.entity.Usuario
import com.sovero.ayudacampus.data.prefs.PreferenciasStore
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Perfil guardado en DataStore (null mientras todavia se esta leyendo)
    val perfilActivo: StateFlow<String?> = prefs.perfilActivo
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa el correo y la contraseña"
            return
        }

        viewModelScope.launch {
            val usuario = repo.login(correo.trim(), clave)

            if (usuario == null) {
                _error.value = "Correo o contraseña incorrectos"
            } else if (usuario.rol != rolSeleccionado.name) {
                if (rolSeleccionado == Rol.CLIENTE) {
                    _error.value = "Esta cuenta no es de estudiante"
                } else {
                    _error.value = "Esta cuenta no es de Soporte TI"
                }
            } else {
                _error.value = null
                prefs.guardarSesion(rolSeleccionado, usuario.id)
                _usuarioActual.value = usuario
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun cerrarSesion() {
        // Primero se limpia el usuario para que el Login no vuelva a entrar solo
        _usuarioActual.value = null
        viewModelScope.launch {
            prefs.cerrarSesion()
        }
    }
}
