# PROMPTS.md — Fase 2: mejora con IA

**Proyecto:** Mi Bodega — App Cliente (tarea complementaria al Laboratorio 06)
**Rama:** `MEJORA-IA`
**Asistente de IA usado:** Gemini en Android Studio

## Mejoras realizadas

**Mejora obligatoria (prompts 1 a 3):** hacer que el campo de búsqueda de la
pantalla de Inicio filtre la lista de productos en tiempo real mientras el
usuario escribe, funcionando junto con el filtro de categorías (los dos filtros
se combinan, no se reemplazan). Archivo modificado: `InicioScreen.kt`.

En la Fase 1 (rama `master`) el campo de búsqueda ya se veía en pantalla, pero
no filtraba nada: la lista solo se filtraba por categoría.

**Mejoras adicionales (prompts 4 y 5):**

- Que los pedidos confirmados aparezcan en la pestaña "Pedidos".
- Reemplazar la pestaña "Categorías" por "Favoritos", porque repetía lo que ya
  hacen los chips de categoría de Inicio.

---

## Prompt 1 — Filtro en tiempo real combinado con la categoría

**Commit:** `660fdb4` Agregar filtro de busqueda en tiempo real combinado con categoria

**Prompt:**

> En InicioScreen.kt tengo un OutlinedTextField cuyo texto se guarda en la
> variable textoBusqueda, pero todavía no filtra nada. La lista
> productosFiltrados solo filtra por categoriaSeleccionada.
>
> Modifica productosFiltrados para que filtre en tiempo real mientras el usuario
> escribe, combinando los dos filtros: un producto se muestra solo si pertenece
> a la categoría seleccionada (o la categoría es "Todos") Y ADEMÁS su nombre
> contiene el texto de búsqueda, sin distinguir mayúsculas de minúsculas. Si el
> texto está vacío, se muestran todos los de la categoría.
>
> No cambies nada más de la pantalla ni agregues ViewModel. Explícame brevemente
> por qué la lista se actualiza sola al escribir.

**Qué generó la IA:**

Separó el filtro en dos condiciones y las unió con `&&`:

```kotlin
val productosFiltrados = productos.filter { producto ->
    val coincideCategoria = categoriaSeleccionada == "Todos" || producto.categoria == categoriaSeleccionada
    val coincideBusqueda = producto.nombre.contains(textoBusqueda, ignoreCase = true)
    coincideCategoria && coincideBusqueda
}
```

**Explicación que dio:** `textoBusqueda` es un estado de Compose. Cada vez que
se escribe una letra el estado cambia, Compose vuelve a ejecutar `InicioScreen`,
el filtro se recalcula con el texto nuevo y el `LazyColumn` se redibuja. Por eso
no hace falta un botón "buscar".

**Qué tuve que corregir:** nada en el código. Lo probé en el emulador con
"Bebidas" + "kola" (queda solo Inca Kola) y con "Snacks" + "kola" (no sale nada)
para comprobar que los dos filtros trabajan juntos.

---

## Prompt 2 — Botón para limpiar y mensaje de sin resultados

**Commit:** `25aae6e` Agregar boton para limpiar busqueda y mensaje de sin resultados

**Prompt:**

> En InicioScreen.kt, mejora el buscador con dos cosas:
>
> 1. Agrega al OutlinedTextField un trailingIcon con un IconButton con el ícono
>    Icons.Default.Close que solo aparezca cuando textoBusqueda no esté vacío, y
>    que al tocarlo deje textoBusqueda en "".
>
> 2. Cuando productosFiltrados esté vacío, hoy se muestra "No hay productos en
>    esta categoría.". Cámbialo para que, si el usuario escribió algo en el
>    buscador, el mensaje diga: No se encontraron productos para "<texto
>    buscado>". Si no escribió nada, deja el mensaje actual.
>
> Usa los colores y estilos que ya usa la pantalla (MaterialTheme, VerdeBodega,
> GrisClaro). No cambies la lógica del filtro.

**Qué generó la IA:**

- Un `trailingIcon` en el `OutlinedTextField` con un `IconButton` (ícono
  `Icons.Default.Close`) que solo se dibuja si `textoBusqueda.isNotEmpty()` y
  que al tocarlo hace `textoBusqueda = ""`.
- Una variable `mensajeVacio` que elige entre los dos textos según si el
  usuario escribió algo o no.
- El import de `androidx.compose.material.icons.filled.Close`.

**Qué tuve que corregir:** nada en el código. Verifiqué que la "X" no aparece
con el buscador vacío y que al tocarla vuelve la lista completa.

---

## Prompt 3 — Ignorar tildes y espacios, y conservar el texto

**Commit:** `650e7fa` Ignorar tildes y espacios en la busqueda y conservar el texto

**Prompt:**

> En InicioScreen.kt, haz dos ajustes al buscador:
>
> 1. Que la búsqueda ignore los espacios al inicio y al final del texto, y que
>    no distinga tildes: si escribo "azucar" debe encontrar "Azúcar Rubia
>    Cartavio", y si escribo "clasicas" debe encontrar "Papas Lay's Clásicas".
>    Hazlo con una función privada pequeña dentro del mismo archivo.
>
> 2. Cambia textoBusqueda de remember a rememberSaveable, igual que
>    categoriaSeleccionada, para que el texto no se pierda al ir al detalle de
>    un producto y volver.
>
> No cambies nada más.

**Qué generó la IA:**

- Una función privada de extensión que quita las tildes:

```kotlin
private fun String.sinTildes(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
```

- En el filtro, la búsqueda se limpia antes de comparar
  (`textoBusqueda.trim().sinTildes()`) y el nombre del producto también pasa
  por `sinTildes()`.
- `textoBusqueda` pasó de `remember` a `rememberSaveable`.

**Qué se tuvo que corregir:**

- La primera versión de la función tenía mal escrito el patrón de las tildes:
  `Regex("\p{Mn}+")` con una sola barra. En Kotlin eso no compila, porque `\p`
  no es un escape válido dentro de un texto; lo correcto es `"\\p{Mn}+"`.
- El mensaje de "No se encontraron productos para..." mostraba el texto con los
  espacios sobrantes. Se ajustó para mostrarlo con `trim()`, igual que lo que
  realmente se busca.
- Quedó un `import androidx.compose.runtime.remember` sin usar, que se quitó.

**Detalle que noté al probar:** como la función quita todos los acentos, la
"ñ" también se trata como "n": escribir "costeno" encuentra "Arroz Costeño".

---

## Prompt 4 — Mostrar los pedidos confirmados en "Pedidos" (Gemini)

**Commit:** `b1efea2` Mostrar los pedidos confirmados en la pantalla Pedidos

**Problema:** al confirmar un pedido, la pestaña "Pedidos" seguía mostrando
"Aún no tienes pedidos", porque esa pantalla no recibía ningún dato.

**Prompt:**

> En mi app de Jetpack Compose (paquete com.tecsup.mibodega.ui.cliente) quiero
> que los pedidos confirmados aparezcan en la pantalla "Pedidos". Hoy
> PedidosScreen.kt solo muestra el mensaje "Aún no tienes pedidos" y nunca
> recibe datos.
>
> Haz estos cambios, con código simple y sin ViewModel ni Room (los pedidos
> viven en memoria con remember, igual que el carrito):
>
> 1. En la carpeta modelo, crea Pedido.kt con una data class Pedido que tenga:
>    id (Int), items (List<ItemCarrito>), total (Double), direccion (String) y
>    metodoPago (String).
>
> 2. En ClienteApp.kt, junto al estado del carrito, agrega:
>    var pedidos by remember { mutableStateOf<List<Pedido>>(emptyList()) }
>
> 3. En ClienteApp.kt, dentro de composable(Rutas.ENTREGA), en
>    onConfirmarPedido: ANTES de la línea carrito = emptyList(), agrega un
>    Pedido nuevo a la lista pedidos usando el carrito actual, el total, la
>    direccion y el metodoPago. El id debe ser pedidos.size + 1. El pedido más
>    nuevo debe quedar primero en la lista.
>
> 4. En ClienteApp.kt, en composable(Rutas.PEDIDOS), pásale la lista:
>    PedidosScreen(pedidos = pedidos, onNavegar = irASeccion).
>
> 5. En PedidosScreen.kt, agrega el parámetro pedidos: List<Pedido>. Si la lista
>    está vacía, deja el mensaje actual tal cual. Si tiene pedidos, muestra un
>    LazyColumn con una Card por pedido que muestre: "Pedido #id", los productos
>    con su cantidad (ejemplo: "2 x Arroz Costeño"), la dirección, el método de
>    pago y el total con formato "S/ %.2f". Usa los mismos colores de la app
>    (VerdeBodega, GrisClaro, MaterialTheme) y actualiza el @Preview.
>
> 6. En ClienteApp.kt, en onCerrarSesion de PerfilScreen, vacía también la
>    lista pedidos.
>
> No cambies la navegación ni ninguna otra pantalla. Mantén el Scaffold, el
> TopAppBar y la BarraInferior de PedidosScreen como están.

**Qué generó la IA:**

- `modelo/Pedido.kt` con la data class pedida.
- En `ClienteApp.kt`: el estado `pedidos`, la creación del pedido al confirmar
  (antes de vaciar el carrito) y el vaciado al cerrar sesión:

```kotlin
val nuevoPedido = Pedido(
    id = pedidos.size + 1,
    items = carrito,
    total = total,
    direccion = direccion,
    metodoPago = metodoPago
)
pedidos = listOf(nuevoPedido) + pedidos
```

- En `PedidosScreen.kt`: un `if` que muestra el mensaje de vacío o un
  `LazyColumn` con un composable privado `PedidoCard` por cada pedido.

**Qué tuve que corregir:**

- El primer intento no se ejecutó: Gemini respondió
  `PERMISSION_DENIED: Verify your account to continue`. No era un problema del
  prompt sino de la cuenta de Google; hubo que resolver eso y reenviarlo.
- En el código no hubo que corregir nada: cumplió los seis puntos y no dejó
  imports sin usar.
- Detalle que quedó: en el `@Preview` puso un pedido de ejemplo con total
  S/ 18.50, pero sus productos (2 Arroz + 1 Leche + delivery) suman S/ 18.20.
  Solo afecta a la vista previa de Android Studio, no a la app.

---

## Prompt 5 — Reemplazar la pestaña "Categorías" por "Favoritos" (Gemini)

**Commit:** `d1a6941` Reemplazar pestaña Categorias por Favoritos

**Motivo:** la pestaña "Categorías" repetía lo que ya hacen los chips de
categoría de Inicio. Además, el corazón de la pantalla de Detalle venía del
esqueleto como un TODO que no hacía nada.

**Prompt:**

> En mi app de Jetpack Compose (paquete com.tecsup.mibodega.ui) quiero
> reemplazar la pestaña "Categorías" del menú inferior por una pestaña
> "Favoritos". Hazlo con código simple, sin ViewModel ni Room: los favoritos
> viven en memoria con remember en ClienteApp.kt, igual que el carrito y los
> pedidos.
>
> 1. En cliente/Rutas.kt: elimina la constante CATEGORIAS y agrega
>    const val FAVORITOS = "favoritos".
>
> 2. En componentes/BarraInferior.kt: cambia el ítem ItemBarra("Categorías",
>    Icons.Default.List, Rutas.CATEGORIAS) por ItemBarra("Favoritos",
>    Icons.Default.Favorite, Rutas.FAVORITOS). Debe quedar en la misma posición
>    (segundo). Arregla los imports.
>
> 3. Elimina el archivo cliente/screens/categorias/CategoriasScreen.kt y su
>    carpeta.
>
> 4. En cliente/ClienteApp.kt:
>    a. Agrega el estado: var favoritos by remember {
>       mutableStateOf<List<Int>>(emptyList()) } (guarda los id de los
>       productos favoritos).
>    b. Quita el composable(Rutas.CATEGORIAS) y el import de CategoriasScreen.
>    c. Agrega composable(Rutas.FAVORITOS) que muestre FavoritosScreen
>       pasándole los productos de listaProductosFake cuyo id esté en favoritos.
>    d. En el composable de Rutas.DETALLE, pásale a DetalleProductoScreen dos
>       parámetros nuevos: esFavorito (si el id del producto está en favoritos)
>       y onToggleFavorito (si ya está en la lista lo quita, si no está lo
>       agrega).
>    e. En onCerrarSesion de PerfilScreen, vacía también la lista favoritos.
>
> 5. En cliente/screens/detalle/DetalleProductoScreen.kt: agrega los parámetros
>    esFavorito: Boolean y onToggleFavorito: () -> Unit, y pásalos a
>    EncabezadoDetalle. El IconButton del corazón (hoy tiene un TODO) debe
>    llamar a onToggleFavorito y mostrar Icons.Default.Favorite con color
>    RojoPrecio si esFavorito es true, o Icons.Default.FavoriteBorder si es
>    false. Actualiza el @Preview.
>
> 6. Crea cliente/screens/favoritos/FavoritosScreen.kt con esta firma:
>    fun FavoritosScreen(favoritos: List<Producto>, onProductoClick: (Producto)
>    -> Unit, onAgregarProducto: (Producto) -> Unit, onNavegar: (String) ->
>    Unit)
>    Debe tener la misma estructura que PedidosScreen.kt: Scaffold con TopAppBar
>    de título "Mis favoritos" y bottomBar = { BarraInferior(rutaActual =
>    Rutas.FAVORITOS, onNavegar = onNavegar) }.
>    - Si la lista está vacía: ícono de corazón centrado, el texto "Aún no
>      tienes favoritos" y debajo "Toca el corazón en un producto para guardarlo
>      aquí.".
>    - Si tiene productos: un LazyColumn que use el componente ProductoCard que
>      ya existe (el mismo de InicioScreen), con onClick = onProductoClick y
>      onAgregar = onAgregarProducto.
>    Incluye un @Preview.
>
> 7. En el composable(Rutas.FAVORITOS) de ClienteApp.kt, conecta onProductoClick
>    para navegar a Rutas.detalle(producto.id) y onAgregarProducto para sumar al
>    carrito con agregarOSumarProducto(carrito, producto, 1), igual que hace
>    InicioScreen. Usa onNavegar = irASeccion.
>
> No cambies InicioScreen.kt, el carrito, los pedidos ni el resto de la
> navegación. Usa los colores que ya tiene la app (VerdeBodega, GrisClaro,
> RojoPrecio, MaterialTheme).

**Qué generó la IA:**

- `Rutas.kt`: cambió `CATEGORIAS` por `FAVORITOS`.
- `BarraInferior.kt`: el segundo ítem ahora es "Favoritos" con ícono de corazón.
- Borró `CategoriasScreen.kt` y su carpeta.
- `FavoritosScreen.kt` nuevo, con mensaje de vacío o un `LazyColumn` que
  reutiliza `ProductoCard`.
- `DetalleProductoScreen.kt`: el corazón ahora funciona y cambia de aspecto.
- `ClienteApp.kt`: el estado `favoritos` y la función que marca o desmarca:

```kotlin
onToggleFavorito = {
    favoritos = if (producto.id in favoritos) {
        favoritos - producto.id
    } else {
        favoritos + producto.id
    }
}
```

**Qué tuve que corregir:**

- Para borrar la carpeta `categorias`, Gemini pidió permiso para ejecutar un
  comando (`Remove-Item -Recurse -Force`). Revisé que la ruta fuera exactamente
  esa carpeta antes de aceptar, y no lo agregué a la lista de comandos
  permitidos para que siga preguntando cada vez.
- En el código no hubo que corregir nada: cumplió los siete puntos.
- Detalle que quedó: el comentario de `BarraInferior.kt` todavía dice que la
  barra se usa en "Inicio, Categorías, Pedidos y Perfil". Es solo un comentario
  desactualizado; no afecta a la app.

---

## Resumen de commits de la Fase 2

| Commit | Asistente | Qué hace |
|---|---|---|
| `660fdb4` | Gemini | El buscador filtra en tiempo real junto con la categoría |
| `25aae6e` | Gemini | Botón "X" para limpiar y mensaje de sin resultados |
| `650e7fa` | Gemini | Ignora tildes y espacios; el texto se conserva al volver |
| `b51dd38` | — | Agrega este archivo PROMPTS.md |
| `b1efea2` | Gemini | Los pedidos confirmados aparecen en "Pedidos" |
| `d1a6941` | Gemini | Reemplaza la pestaña "Categorías" por "Favoritos" |
