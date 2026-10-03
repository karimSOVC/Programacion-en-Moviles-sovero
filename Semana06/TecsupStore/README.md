# Laboratorio 06 - Menú y Navegación (TECSUP Store)

**Curso:** Programación en Móviles - 4to ciclo
**Docente:** Juan José León Suiyon
**Alumno:** Karim Sovero

## Objetivo

Agregar a la TECSUP Store dos piezas nuevas:

- Un **DropdownMenu** contextual (menú de tres puntos) en cada tarjeta de producto, con íconos y divisores.
- Un **NavigationDrawer** como navegación principal, con encabezado de usuario e ítem activo resaltado.

Además, trabajé con GitHub en dos fases: desarrollo propio sin IA y mejora con IA.

## Ramas del repositorio

| Rama | Contenido |
|------|-----------|
| `master` | Fase 1: desarrollo sin IA (es la rama por defecto del repo) |
| `mejora-ia` | Fase 2: mejora con IA (Gemini en Android Studio) |
| `mejora-ia-lab03` | Rama vieja del Lab 03, la renombré porque el nombre `mejora-ia` ya estaba ocupado |

## Estructura de archivos

- `Producto.kt`: modelo de datos del producto.
- `TarjetaProducto.kt`: tarjeta de producto con el ícono de tres puntos y el DropdownMenu.
- `AppDrawer.kt`: contenido del NavigationDrawer (encabezado, destinos y badge).
- `AppNavegacion.kt`: envuelve la app con el ModalNavigationDrawer y guarda el estado de favoritos.
- `Pantallas.kt`: pantallas de Mis pedidos, Favoritos y Perfil.
- `MainActivity.kt`: punto de entrada y PantallaInicio con la lista de productos.
- `PROMPTS.md`: prompts usados en la Fase 2.

## Fase 1 - Desarrollo sin IA (rama master)

| Commit | Qué se hizo |
|--------|-------------|
| 1 | Se creó el proyecto TecsupStore con la plantilla Empty Activity de Compose |
| 2 | Se agregó el modelo Producto, la TarjetaProducto y PantallaInicio con la lista de productos |
| 3 | Se agregó el ícono de tres puntos y el estado `expanded` en la tarjeta |
| 4 | Se agregó el DropdownMenu con las opciones Favoritos, Compartir y Reportar |
| 5 | Se agregaron íconos y un divisor al DropdownMenu |
| 6 | Se agregó el NavigationDrawer con ModalDrawerSheet y 4 destinos |
| 7 | Se agregó la navegación real desde los ítems del drawer |
| 8 | Se agregó el encabezado de usuario y el ítem activo resaltado en el drawer |

## Fase 2 - Mejora con IA (rama mejora-ia)

Mejora obligatoria: un **badge con contador** en el ítem "Favoritos" del drawer, que muestra cuántos productos se marcaron como favoritos desde el DropdownMenu de cada producto.

Usé **Gemini en Android Studio**. En esta fase también agregué mejoras de diseño y una pestaña Favoritos que lista los productos marcados.

## Prompts utilizados

### Prompt 1 - Estado de favoritos

```
Estoy haciendo una app Android con Kotlin y Jetpack Compose (Material3). Paquete: com.sovero.tecsupstore_sovero.
Tengo una lista de productos. Cada tarjeta (TarjetaProducto.kt) tiene un menu de tres puntos con un DropdownMenu con las opciones Favoritos, Compartir y Reportar. Tambien tengo un NavigationDrawer (AppDrawer.kt) dentro de AppNavegacion.kt, y la lista de productos esta en PantallaInicio dentro de MainActivity.kt.
Necesito SOLO el primer paso de una mejora: guardar que productos son favoritos y hacer que la opcion Favoritos del DropdownMenu marque y desmarque el producto. NO agregues todavia el badge ni el contador en el drawer.
Requisitos:
- El estado de favoritos debe vivir en AppNavegacion (state hoisting), como un mutableStateListOf<Int> con los id de los productos favoritos.
- TarjetaProducto recibe esFavorito: Boolean y onToggleFavorito: () -> Unit.
- Si el producto ya es favorito, la opcion del menu dice "Quitar de favoritos"; si no, dice "Agregar a favoritos".
- PantallaInicio recibe la lista de ids favoritos y el callback para alternar un favorito, y se los pasa a cada TarjetaProducto.
- No uses librerias nuevas ni ViewModel.
- Codigo limpio y sin comentarios.
- Modifica solo los archivos necesarios y explica en pocas lineas que cambiaste.
```

**Qué hace:** guarda en una lista los id de los productos favoritos y hace que la opción Favoritos del menú de tres puntos marque y desmarque el producto. El texto de la opción cambia entre "Agregar a favoritos" y "Quitar de favoritos". Todavía no muestra nada en el drawer.

### Prompt 2 - Corazón en la tarjeta

```
En TarjetaProducto.kt, cuando el producto es favorito (esFavorito = true), muestra un icono de corazon (Icons.Filled.Favorite) con color MaterialTheme.colorScheme.primary junto al precio del producto. Si no es favorito, no muestra nada. No cambies nada mas, no agregues librerias y deja el codigo limpio y sin comentarios.
```

**Qué hace:** muestra un corazón junto al precio de los productos marcados como favoritos, y lo quita cuando se desmarcan. Hice este prompt porque en el primero Gemini no puso el corazón.

### Prompt 3 - Badge con contador en el drawer

```
En mi app Android con Kotlin y Jetpack Compose (Material3), ya tengo el estado de favoritos en AppNavegacion.kt como un mutableStateListOf<Int> con los id de los productos favoritos. AppDrawer.kt recibe destinoActual y onItemClick, y muestra los destinos Inicio, Mis pedidos, Favoritos y Perfil con NavigationDrawerItem.

Necesito que el item "Favoritos" del drawer muestre un badge con la cantidad de productos favoritos.

Requisitos:
- AppDrawer recibe un nuevo parametro cantidadFavoritos: Int.
- AppNavegacion le pasa a AppDrawer el tamaño de la lista de favoritos.
- Solo el item "Favoritos" muestra el badge, usando el parametro badge de NavigationDrawerItem con un Text con el numero.
- Si la cantidad es 0, no se muestra el badge.
- El numero debe actualizarse solo al marcar o quitar favoritos desde el menu de tres puntos de cada producto.
- No uses librerias nuevas ni ViewModel.
- Codigo limpio y sin comentarios.
- Modifica solo los archivos necesarios y explica en pocas lineas que cambiaste.
```

**Qué hace:** agrega un número (badge) al ítem "Favoritos" del drawer con la cantidad de productos favoritos. Si no hay ninguno, no se muestra, y el número se actualiza solo al marcar o quitar favoritos. Esta es la mejora obligatoria del laboratorio.

### Prompt 4 - Tema morado y barra superior

```
En mi app Android con Kotlin y Jetpack Compose (Material3), paquete com.sovero.tecsupstore_sovero, quiero una paleta morada fija. En ui/theme/Theme.kt desactiva el dynamic color (dynamicColor = false) y define un esquema claro con primary morado oscuro (aprox #5B2A86), onPrimary blanco, primaryContainer lila claro (aprox #EDE7F6) y onPrimaryContainer morado oscuro. En AppNavegacion.kt la barra superior debe tener fondo primary y texto blanco, con el titulo "TECSUP Store" en negrita y debajo un subtitulo pequeño "Mas vendidos" (solo en Inicio; en las otras pantallas solo el nombre de la pantalla). Conserva el icono de menu que abre el drawer. No cambies la logica de favoritos ni la navegacion. Sin librerias nuevas, codigo limpio y sin comentarios. Modifica solo los archivos necesarios y explica en pocas lineas que cambiaste.
```

**Qué hace:** fija una paleta morada en toda la app (sin los colores dinámicos del sistema) y cambia la barra superior a fondo morado con el título "TECSUP Store" y el subtítulo "Mas vendidos".

### Prompt 5 - Diseño de la tarjeta de producto

```
En TarjetaProducto.kt cambia solo el diseño visual: la tarjeta usa containerColor primaryContainer con esquinas redondeadas. A la izquierda va un cuadro redondeado de unos 56dp, con fondo un poco mas claro, que contiene el icono de bolsa (Icons.Filled.ShoppingBag) en morado oscuro. Al centro va el nombre en negrita y debajo el precio con color primary. El icono de tres puntos va arriba a la derecha de la tarjeta. Conserva el DropdownMenu, los parametros esFavorito y onToggleFavorito y el corazon de favoritos junto al precio. Sin librerias nuevas, codigo limpio y sin comentarios. Modifica solo los archivos necesarios y explica en pocas lineas que cambiaste.
```

**Qué hace:** rediseña la tarjeta para que se parezca a la figura del laboratorio: un cuadro con el ícono de bolsa a la izquierda, el nombre en negrita, el precio en morado y el menú de tres puntos arriba a la derecha. No cambia la lógica de favoritos.

### Prompt 6 - Divisores en el menú de tres puntos

```
En el DropdownMenu de TarjetaProducto.kt agrega un HorizontalDivider entre cada par de opciones (entre Favoritos y Compartir, y entre Compartir y Reportar). Conserva los iconos, los textos y la logica actual. Sin librerias nuevas, codigo limpio y sin comentarios. Explica en pocas lineas que cambiaste.
```

**Qué hace:** agrega una línea divisoria entre todas las opciones del menú de tres puntos, para que se vea como en la figura del laboratorio.

### Prompt 7 - Diseño del drawer

```
En AppDrawer.kt cambia el diseño: el encabezado tiene un circulo lila con las iniciales en negrita y a la derecha el nombre y el correo (mantén los datos actuales del usuario), con un HorizontalDivider debajo. El item activo debe tener fondo primaryContainer y texto en negrita morado. Agrega al final un item "Cerrar sesion" con icono Icons.AutoMirrored.Filled.ExitToApp que solo cierre el drawer. Conserva el badge del contador de favoritos y los parametros actuales de AppDrawer. Sin librerias nuevas, codigo limpio y sin comentarios. Modifica solo los archivos necesarios y explica en pocas lineas que cambiaste.
```

**Qué hace:** mejora el drawer con un encabezado de iniciales en un círculo lila, el ítem activo resaltado en lila y un ítem "Cerrar sesion" al final que solo cierra el menú. Mantiene el badge de favoritos.

### Prompt 8 - Pestaña Favoritos con los productos marcados

```
En mi app Android con Kotlin y Jetpack Compose (Material3), paquete com.sovero.tecsupstore_sovero, el estado de favoritos vive en AppNavegacion.kt como un mutableStateListOf<Int> con los id de los productos favoritos. La lista de productos esta dentro de PantallaInicio en MainActivity.kt, y PantallaFavoritos en Pantallas.kt solo muestra un texto de relleno.

Necesito que la pantalla Favoritos muestre las tarjetas de los productos marcados como favoritos.

Requisitos:
- Mueve la lista de productos a un lugar compartido (por ejemplo, un val de nivel superior en Producto.kt) para que PantallaInicio y PantallaFavoritos usen la misma lista.
- PantallaFavoritos recibe la lista de ids favoritos y el callback para alternar un favorito.
- Muestra en un LazyColumn solo los productos cuyo id esta en favoritos, usando TarjetaProducto.
- Si no hay favoritos, muestra un texto centrado: "Aun no tienes productos favoritos".
- Desde el menu de tres puntos de esas tarjetas se puede quitar el favorito, y el producto desaparece de la lista al instante.
- AppNavegacion le pasa a PantallaFavoritos los parametros necesarios.
- No cambies el diseño ni la logica del badge. Sin librerias nuevas, codigo limpio y sin comentarios.
- Modifica solo los archivos necesarios y explica en pocas lineas que cambiaste.
```

**Qué hace:** hace que la pestaña Favoritos del drawer muestre de verdad los productos que marqué desde el menú de tres puntos. Si quito uno, desaparece de la lista al instante, y si no hay ninguno sale el mensaje "Aun no tienes productos favoritos".

## Preguntas de reflexión

**1. ¿Por qué el DropdownMenu se declara dentro de un Box junto al ícono que lo activa y no en cualquier parte de la pantalla?**

El DropdownMenu se muestra pegado al elemento donde está declarado. Si lo pongo dentro de un Box junto al ícono de tres puntos, el menú se abre justo debajo de ese ícono. Si lo declarara en otra parte de la pantalla, saldría en un lugar que no tiene relación con el botón que lo abrió.

**2. ¿Qué diferencia de alcance hay entre las opciones del DropdownMenu y las del NavigationDrawer?**

Las opciones del DropdownMenu afectan a un solo producto, el de la tarjeta donde toqué los tres puntos. Por ejemplo, "Favoritos" marca solo ese producto. En cambio, las opciones del NavigationDrawer afectan a toda la app, porque me llevan a otra pantalla completa como Mis pedidos o Perfil.

**3. ¿Cómo tuve que estructurar mi código para que el contador de favoritos del drawer se entere de lo que pasa en el DropdownMenu de cada producto?**

Tuve que subir el estado de favoritos a `AppNavegacion`, que es el archivo que tiene tanto el drawer como las pantallas. Ahí guardo una lista con los id de los favoritos. Esa lista se pasa hacia abajo a las tarjetas, y cada tarjeta avisa hacia arriba con un callback (`onToggleFavorito`) cuando toco la opción. Como el drawer lee el tamaño de la misma lista, el número se actualiza solo. Esto se llama state hoisting.

**4. ¿Qué tuve que corregir del código que me generó la IA para la mejora del badge de favoritos?**

En el primer prompt Gemini hizo que la opción del menú marcara y desmarcara el favorito, pero no puso el corazón visible en la tarjeta, así que tuve que hacer un segundo prompt para pedirlo. Antes de hacer cada commit corrí la app para revisar que el comportamiento fuera el esperado.

## Observaciones

1. La rama `mejora-ia` ya existía en GitHub porque era del Lab 03. Como tenía otro historial, no me dejaba subir la mía. La renombré a `mejora-ia-lab03` para conservar ese trabajo y poder crear la nueva con el nombre que pide el laboratorio.
2. Git me mostró un aviso de que los saltos de línea (LF) se cambiarían a CRLF en Windows. Es solo un aviso y no afectó al proyecto.
3. Un commit me salió con el mensaje cortado porque al pegarlo se coló un salto de línea dentro de las comillas. Como todavía no había hecho push, lo corregí con `git commit --amend`.
4. Al hacer `git status` aparecían archivos de la carpeta `.idea/` sin seguimiento. Para no subirlos por error agregué solo el código con `git add app/src` en vez de `git add .`.

## Conclusiones

1. En la Fase 1 sin IA tuve que investigar y escribir cada pieza yo mismo, y eso me ayudó a entender cómo funcionan el DropdownMenu, el estado `expanded` y el ModalNavigationDrawer. Me tomó más tiempo, pero entendí por qué cada cosa va donde va.
2. En la Fase 2 con IA avancé más rápido, pero no todo salió completo a la primera, como el corazón que no apareció. Aprendí que hay que probar la app y revisar lo que genera la IA antes de hacer commit, y que un prompt corto y específico da mejores resultados que uno largo con varias cosas a la vez.
3. Lo más importante que aprendí fue el state hoisting: poner el estado en un solo lugar y pasarlo hacia abajo permite que dos partes distintas de la app, el menú del producto y el drawer, se mantengan conectadas.

## Capturas

![DropdownMenu abierto sobre un producto](capturas/dropdown.png)

![NavigationDrawer con el badge de favoritos](capturas/drawer.png)

![Pantalla Favoritos con los productos marcados](capturas/favoritos.png)
