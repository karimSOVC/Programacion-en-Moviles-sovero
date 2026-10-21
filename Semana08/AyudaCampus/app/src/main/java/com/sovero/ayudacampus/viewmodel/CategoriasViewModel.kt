package com.sovero.ayudacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.ayudacampus.data.local.entity.Categoria
import com.sovero.ayudacampus.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoriasViewModel(private val repo: AyudaRepository) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> = repo.observarCategorias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mensaje para la pantalla (por ejemplo, si no se puede eliminar)
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    // true cuando se guardo o elimino, para volver a la lista
    private val _listo = MutableStateFlow(false)
    val listo: StateFlow<Boolean> = _listo

    fun alternar(c: Categoria) {
        viewModelScope.launch { repo.cambiarActiva(c.id, !c.activa) }
    }

    // Nueva (id = 0) o editada (id existente)
    fun guardar(categoria: Categoria) {
        if (categoria.nombre.isBlank()) {
            _mensaje.value = "El nombre es obligatorio"
            return
        }
        viewModelScope.launch {
            repo.guardarCategoria(categoria.copy(nombre = categoria.nombre.trim()))
            _listo.value = true
        }
    }

    // No se puede eliminar una categoria que ya tiene tickets
    fun eliminar(categoria: Categoria) {
        viewModelScope.launch {
            val conteos = repo.observarCantidadPorCategoria().first()
            val cantidad = conteos.firstOrNull { it.nombre == categoria.nombre }?.cantidad ?: 0
            if (cantidad > 0) {
                _mensaje.value = "No se puede eliminar: la categoría tiene $cantidad tickets"
            } else {
                repo.eliminarCategoria(categoria)
                _listo.value = true
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
