package com.sovero.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sovero.murocampus.data.PostRepository
import com.sovero.murocampus.model.Comentario
import com.sovero.murocampus.model.NuevoPost
import com.sovero.murocampus.model.Post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Solo conoce la interface PostRepository: no sabe de dónde vienen los
 * datos ni cómo se piden a internet. Convierte cada Result en un UiState.
 */
class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    // ----- Lista (paso 6)
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    // ----- Filtro por usuario (paso 8): null significa Todos
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    // ----- Mejora (Fase 2): búsqueda por título
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    /**
     * Lo que muestra la lista: las publicaciones ya descargadas filtradas
     * por el texto de búsqueda. No vuelve a pedir nada a internet.
     */
    val listaVisible: StateFlow<UiState<List<Post>>> =
        combine(_lista, _busqueda) { estado, texto ->
            if (estado is UiState.Exito && texto.isNotBlank()) {
                UiState.Exito(estado.datos.filter { it.title.contains(texto.trim(), ignoreCase = true) })
            } else {
                estado
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Cargando)

    /** Guarda lo que se escribe en el campo de búsqueda. */
    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    // Al crearse el ViewModel ya empieza a cargar. Va después de los
    // StateFlow que usa cargar(): Kotlin los crea en orden, de arriba abajo
    init { cargar() }

    /**
     * Pide las publicaciones según el filtro actual; también la usa el
     * botón Reintentar. Sin filtro: todas. Con filtro: solo las de ese usuario.
     */
    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            val userId = _usuarioFiltro.value
            val resultado = if (userId == null) repo.listar() else repo.porUsuario(userId)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Guarda el filtro elegido en los chips y vuelve a cargar la lista. */
    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    // ----- Detalle con comentarios (paso 7)
    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    /**
     * Pide la publicación y sus comentarios al mismo tiempo (async).
     * Si cualquiera de las dos falla, todo el detalle muestra el error.
     */
    fun abrir(id: Int) {
        viewModelScope.launch {
            _detalle.value = UiState.Cargando
            val (resPost, resComentarios) = coroutineScope {
                val a = async { repo.detalle(id) }
                val b = async { repo.comentarios(id) }
                Pair(a.await(), b.await())
            }
            val post = resPost.getOrElse {
                _detalle.value = UiState.Error(mensajeDe(it))
                return@launch
            }
            val comentarios = resComentarios.getOrElse {
                _detalle.value = UiState.Error(mensajeDe(it))
                return@launch
            }
            _detalle.value = UiState.Exito(DetalleUi(post, comentarios))
        }
    }

    // ----- Crear publicación (paso 9): null = todavía no se envió
    private val _creacion = MutableStateFlow<UiState<Post>?>(null)
    val creacion: StateFlow<UiState<Post>?> = _creacion.asStateFlow()

    /** Envía la publicación nueva con POST y guarda la respuesta. */
    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _creacion.value = UiState.Cargando
            val nuevo = NuevoPost(userId = 1, title = titulo, body = texto)
            repo.crear(nuevo)
                .onSuccess { _creacion.value = UiState.Exito(it) }
                .onFailure { _creacion.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Deja el formulario como nuevo al volver a abrir la pantalla Crear. */
    fun limpiarCreacion() {
        _creacion.value = null
    }
}

/** Lo que muestra la pantalla de detalle: la publicación y sus comentarios. */
data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)
