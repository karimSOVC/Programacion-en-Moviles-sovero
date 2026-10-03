# PROMPTS.md — Fase 2: mejora con IA

**Proyecto:** Mi Bodega — App Cliente (tarea complementaria al Laboratorio 06)
**Rama:** `MEJORA-IA`
**Asistente de IA usado:** Claude (Claude Code)
**Archivo modificado:** `app/src/main/java/com/tecsup/mibodega/ui/cliente/screens/inicio/InicioScreen.kt`

## Mejora realizada

Hacer que el campo de búsqueda de la pantalla de Inicio filtre la lista de
productos en tiempo real mientras el usuario escribe, funcionando junto con el
filtro de categorías (los dos filtros se combinan, no se reemplazan).

En la Fase 1 (rama `master`) el campo de búsqueda ya se veía en pantalla, pero
no filtraba nada: la lista solo se filtraba por categoría.

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

## Resumen de commits de la Fase 2

| Commit | Qué hace |
|---|---|
| `660fdb4` | El buscador filtra en tiempo real junto con la categoría |
| `25aae6e` | Botón "X" para limpiar y mensaje de sin resultados |
| `650e7fa` | Ignora tildes y espacios; el texto se conserva al volver |
| (este) | Agrega este archivo PROMPTS.md |
