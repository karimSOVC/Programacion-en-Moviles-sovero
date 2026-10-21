# Prompts utilizados

Fase 2 del laboratorio 08 (AyudaCampus), rama `MEJORA-IA`.

**IA utilizada:** Claude (Claude Code), no Gemini. Las mejoras se pidieron por chat y la IA escribió el código directamente en el proyecto; después revisé cada cambio contra las reglas de MVVM.

## Prompt 1

**Texto enviado a la IA:**

```
Tengo una app Kotlin con Jetpack Compose, Material 3, MVVM, Room y un
AyudaRepository (interface), paquete com.sovero.ayudacampus.
Agrega en el panel de soporte el tiempo promedio de resolucion de los
tickets cerrados (fechaCierre - fecha).
La consulta va en TicketDao (data/local/dao/TicketDao.kt), la funcion se
declara primero en AyudaRepository y luego se implementa en
AyudaRepositoryRoom, y el valor se expone como StateFlow en PanelViewModel.
PanelScreen solo lo muestra en una tarjeta, usando MaterialTheme y sin
colores fijos. La View no debe usar el DAO ni el Repository.
No uses XML. Muestrame solo los archivos que cambian.
```

**Qué generó la IA:**
- `TicketDao`: consulta `promedioResolucion()` con `SELECT AVG(fechaCierre - fecha)` de los tickets CERRADOS, devuelve `Flow<Double?>`.
- `AyudaRepository`: declaró `observarTiempoPromedioResolucion()` en la interface y luego la implementó en `AyudaRepositoryRoom`.
- `PanelViewModel`: `tiempoPromedio: StateFlow<String>` con el promedio convertido a texto ("24 h 0 min", "1 d 3 h") o "Sin tickets cerrados".
- `PanelScreen`: tarjeta "Tiempo promedio de resolución" debajo de los contadores, con colores de `MaterialTheme`.

**Qué tuve que corregir yo:** nada. La IA usó `Double?` para el resultado de `AVG` (en SQLite es un número decimal y con `Long` se perdería la parte decimal).

**¿Respeta MVVM?:** Sí. La consulta está en el DAO, la función se declaró primero en la interface, el ViewModel solo usa `AyudaRepository` y la pantalla solo muestra el texto del `StateFlow`.

**Commit:** `ia: agrega tiempo promedio de resolucion al panel`

## Prompt 2

**Texto enviado a la IA:**

```
sigamos con COMMIT B
```

(Referencia: la mejora "Contador de SLA" de la guía: cada ticket muestra el tiempo restante según su prioridad, Alta 4 h, Media 24 h, Baja 72 h. Capas: ViewModel + View.)

**Qué generó la IA:**
- `viewmodel/Sla.kt` (nuevo): la regla de horas por prioridad, `calcularSla()` (solo para tickets ABIERTO o EN_PROCESO) y `relojPorMinuto()`, un `Flow` que emite la hora cada minuto.
- `MisTicketsViewModel` y `BandejaViewModel`: `sla: StateFlow<Map<Int, InfoSla>>` combinando los tickets con el reloj.
- `TicketCard`: dos parámetros opcionales (`sla`, `slaVencido`) que muestran "Quedan 2 h 10 min" o "Vencido hace 1 h" en rojo.
- `MisTicketsScreen` y `BandejaScreen`: le pasan el SLA a cada tarjeta.

**Qué tuve que corregir yo:** nada en el código final. La primera versión devolvía un `MutableMap` dentro de `combine` y `stateIn` esperaba un `Map`, lo que no compilaba; la IA lo detectó al revisar y lo cambió a `mapa.toMap()` antes del commit.

**¿Respeta MVVM?:** Sí. El cálculo del SLA vive en la capa ViewModel y las pantallas solo reciben el texto ya calculado. No se tocó el DAO ni el Repository porque no hacía falta.

**Commit:** `ia: agrega contador de SLA por prioridad`

## Prompt 3

**Texto enviado a la IA:**

```
continuemos con el commit C
```

(Referencia: la mejora "Búsqueda avanzada en la bandeja" de la guía: Soporte filtra por prioridad, categoría y texto al mismo tiempo. Capas: ViewModel + View.)

**Qué generó la IA:**
- `BandejaViewModel`: filtros de estado, prioridad, categoría y texto combinados con `combine` de 5 flujos, conservando el orden por prioridad del DAO; funciones `elegirPrioridad`, `elegirCategoria`, `buscar` y `limpiarFiltros`.
- `BandejaScreen`: buscador (título, número o cliente), tres filas de chips (estado, prioridad, categoría con ícono), cantidad de resultados y botón "Limpiar filtros".

**Qué tuve que corregir yo:** nada.

**¿Respeta MVVM?:** Sí. Todo el filtrado está en el ViewModel; la pantalla solo avisa qué chip se tocó y muestra la lista resultante.

**Commit:** `ia: agrega busqueda avanzada en la bandeja`

## Reflexion (5 lineas)

- **Qué mejoró la IA y qué no:** agregó las 3 mejoras en las capas correctas y siguió el estilo del proyecto; lo que no pudo hacer fue compilar desde su entorno, así que cada cambio había que probarlo en Android Studio.
- **Qué error cometió:** en el SLA devolvió un `MutableMap` donde se esperaba un `Map`; lo corrigió antes del commit.
- **Qué aprendí sobre MVVM:** si la interface `AyudaRepository` es el único contrato, una mejora nueva solo agrega una función ahí y el resto de capas no se rompe.
- **Qué aprendí sobre Room:** una consulta con `AVG` devuelve un decimal que puede ser `null` cuando no hay filas, por eso el tipo correcto es `Double?`.
- **Qué aprendí sobre reglas de negocio:** reglas como el SLA o los filtros van en el ViewModel; así la pantalla solo muestra y la regla se cambia en un solo lugar.
