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

    // ----- Mejora (Fase 2): paginación de 20 en 20
    private val _hayMas = MutableStateFlow(false)
    val hayMas: StateFlow<Boolean> = _hayMas.asStateFlow()

    private val _cargandoMas = MutableStateFlow(false)
    val cargandoMas: StateFlow<Boolean> = _cargandoMas.asStateFlow()

    // Mensaje si falla "Cargar más" (la lista que ya estaba se conserva)
    private val _errorMas = MutableStateFlow<String?>(null)
    val errorMas: StateFlow<String?> = _errorMas.asStateFlow()

    // Al crearse el ViewModel ya empieza a cargar. Va después de los
    // StateFlow que usa cargar(): Kotlin los crea en orden, de arriba abajo
    init { cargar() }

    /**
     * Pide las publicaciones según el filtro actual; también la usa el
     * botón Reintentar. Sin filtro: la primera página de 20 (mejora de la
     * Fase 2). Con filtro: las de ese usuario (son 10, no se paginan).
     */
    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            _errorMas.value = null
            val userId = _usuarioFiltro.value
            val resultado = if (userId == null) {
                repo.pagina(inicio = 0, limite = TAMANO_PAGINA)
            } else {
                repo.porUsuario(userId)
            }
            resultado
                .onSuccess {
                    _lista.value = UiState.Exito(it)
                    // Si llegó una página llena, puede haber más
                    _hayMas.value = userId == null && it.size == TAMANO_PAGINA
                }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Mejora (Fase 2): pide la página siguiente y la agrega al final. */
    fun cargarMas() {
        val actual = _lista.value as? UiState.Exito ?: return
        if (_cargandoMas.value || !_hayMas.value) return

        viewModelScope.launch {
            _cargandoMas.value = true
            _errorMas.value = null
            repo.pagina(inicio = actual.datos.size, limite = TAMANO_PAGINA)
                .onSuccess { nuevos ->
                    _lista.value = UiState.Exito(actual.datos + nuevos)
                    _hayMas.value = nuevos.size == TAMANO_PAGINA
                }
                .onFailure { _errorMas.value = mensajeDe(it) }
            _cargandoMas.value = false
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
        // Mejora (Fase 2): cada detalle empieza sin el mensaje de borrado
        _borrado.value = null
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

    // ----- Mejora (Fase 2): borrar publicación. null = no se intentó
    private val _borrado = MutableStateFlow<UiState<Int>?>(null)
    val borrado: StateFlow<UiState<Int>?> = _borrado.asStateFlow()

    /**
     * Borra la publicación en el servidor. Si responde bien, también la
     * quita de la lista que ya está en pantalla, sin volver a descargarla.
     */
    fun borrar(id: Int) {
        viewModelScope.launch {
            _borrado.value = UiState.Cargando
            repo.borrar(id)
                .onSuccess { codigo ->
                    _borrado.value = UiState.Exito(codigo)
                    val actual = _lista.value
                    if (actual is UiState.Exito) {
                        _lista.value = UiState.Exito(actual.datos.filter { it.id != id })
                    }
                }
                .onFailure { _borrado.value = UiState.Error(mensajeDe(it)) }
        }
    }

    // ----- Mejora (Fase 2): editar el título. null = no se envió
    private val _edicion = MutableStateFlow<UiState<Post>?>(null)
    val edicion: StateFlow<UiState<Post>?> = _edicion.asStateFlow()

    /**
     * Envía el título nuevo con PATCH. Si responde bien, también cambia el
     * título en la lista y en el detalle que ya están en pantalla.
     */
    fun editarTitulo(id: Int, titulo: String) {
        viewModelScope.launch {
            _edicion.value = UiState.Cargando
            repo.editarTitulo(id, titulo)
                .onSuccess { post ->
                    _edicion.value = UiState.Exito(post)
                    val lista = _lista.value
                    if (lista is UiState.Exito) {
                        _lista.value = UiState.Exito(
                            lista.datos.map { if (it.id == id) it.copy(title = post.title) else it }
                        )
                    }
                    val detalle = _detalle.value
                    if (detalle is UiState.Exito && detalle.datos.post.id == id) {
                        _detalle.value = UiState.Exito(
                            detalle.datos.copy(post = detalle.datos.post.copy(title = post.title))
                        )
                    }
                }
                .onFailure { _edicion.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Deja la pantalla Editar sin respuesta al abrirla. */
    fun limpiarEdicion() {
        _edicion.value = null
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

    companion object {
        // Mejora (Fase 2): cuántas publicaciones trae cada página
        const val TAMANO_PAGINA = 20
    }
}

/** Lo que muestra la pantalla de detalle: la publicación y sus comentarios. */
data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)
