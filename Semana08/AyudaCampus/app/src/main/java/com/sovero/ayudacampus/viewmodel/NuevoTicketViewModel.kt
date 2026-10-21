package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.local.entity.Categoria
import com.sovero.ayudacampus.data.prefs.PreferenciasStore
import com.sovero.ayudacampus.data.repository.AyudaRepository
import com.sovero.ayudacampus.model.Prioridad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NuevoTicketViewModel(
    private val repo: AyudaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    // Solo las categorias activas (las inactivas NO aparecen)
    val categorias: StateFlow<List<Categoria>> = repo.observarCategoriasActivas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estados del formulario
    private val _categoriaId = MutableStateFlow<Int?>(null)
    val categoriaId: StateFlow<Int?> = _categoriaId

    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> = _titulo

    private val _ubicacion = MutableStateFlow("")
    val ubicacion: StateFlow<String> = _ubicacion

    private val _prioridad = MutableStateFlow(Prioridad.MEDIA) // Media por defecto
    val prioridad: StateFlow<Prioridad> = _prioridad

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion

    // Errores por campo
    private val _errorCategoria = MutableStateFlow<String?>(null)
    val errorCategoria: StateFlow<String?> = _errorCategoria

    private val _errorTitulo = MutableStateFlow<String?>(null)
    val errorTitulo: StateFlow<String?> = _errorTitulo

    private val _errorDescripcion = MutableStateFlow<String?>(null)
    val errorDescripcion: StateFlow<String?> = _errorDescripcion

    // Se pone en true cuando el ticket se guardo, para volver a Mis tickets
    private val _enviado = MutableStateFlow(false)
    val enviado: StateFlow<Boolean> = _enviado

    fun elegirCategoria(id: Int) {
        _categoriaId.value = id
        _errorCategoria.value = null
    }

    fun cambiarTitulo(texto: String) {
        _titulo.value = texto
        _errorTitulo.value = null
    }

    fun cambiarUbicacion(texto: String) {
        _ubicacion.value = texto
    }

    fun cambiarPrioridad(p: Prioridad) {
        _prioridad.value = p
    }

    fun cambiarDescripcion(texto: String) {
        _descripcion.value = texto
        _errorDescripcion.value = null
    }

    fun enviar() {
        var valido = true

        // La categoria debe estar elegida y seguir activa
        val idElegido = _categoriaId.value
        if (idElegido == null || categorias.value.none { it.id == idElegido }) {
            _errorCategoria.value = "Elige una categoría"
            valido = false
        }
        if (_titulo.value.trim().length < 5) {
            _errorTitulo.value = "El título debe tener al menos 5 caracteres"
            valido = false
        }
        if (_descripcion.value.trim().length < 10) {
            _errorDescripcion.value = "La descripción debe tener al menos 10 caracteres"
            valido = false
        }

        if (!valido || idElegido == null) return

        viewModelScope.launch {
            val usuarioId = prefs.usuarioId.first()
            repo.crearTicket(
                clienteId = usuarioId,
                categoriaId = idElegido,
                titulo = _titulo.value.trim(),
                descripcion = _descripcion.value.trim(),
                ubicacion = _ubicacion.value.trim(),
                prioridad = _prioridad.value
            )
            _enviado.value = true
        }
    }
}
