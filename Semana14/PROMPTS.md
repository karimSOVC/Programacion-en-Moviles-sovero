# PROMPTS — CentroTareas (Fase 2 · rama MEJORA-IA)

Herramienta usada: **Claude Code**. Las tres mejoras respetan MVVM + Repository y no
usan `GlobalScope`, `runBlocking` ni `Thread.sleep`.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Agrega un tiempo máximo a las cargas con `withTimeout`: si tardan más de 2 s, que muestre "Tardó demasiado".» | Propuso envolver las cargas en `withTimeout(2000)` y atrapar la `TimeoutCancellationException` para mostrar el error. | Agregué un interruptor «Tiempo máximo 2 s», porque los pedidos tardan 3 s y con el límite siempre puesto nunca se veía el recuadro verde. Puse el `catch` de `TimeoutCancellationException` **antes** del de `CancellationException` y saqué el límite a una función `conLimite` para no repetirlo en las dos cargas. | `TimeoutCancellationException` es hija de `CancellationException`: si la atrapo después, la cancelación se la come y nunca sale «Tardó demasiado». El interruptor permite probar los dos casos. |
| «Muestra el porcentaje del cálculo de primos mientras avanza.» | Propuso que `contarPrimos` reciba una función `alAvanzar` y la llame en cada número, y que el ViewModel guarde el avance en un `StateFlow<Float>`. | Cambié para que avise cada 1 % y no en cada número. Dejé `alAvanzar` con valor por defecto en la interface, así el cálculo sigue funcionando igual que en la Fase 1. La barra se muestra solo mientras calcula y al cancelar dice en qué porcentaje se quedó. | Con 500 000 números serían 500 000 actualizaciones de la pantalla y el cálculo se volvía más lento. Con el 1 % son solo 100. El cálculo sigue en el Repository, como pide la arquitectura. |
| «Agrega una tabla que acumule los tiempos de "Una por una" y "A la vez" para ver la diferencia.» | Propuso guardar dos listas de tiempos en el ViewModel y dibujar la tabla con un `Row` por intento, calculando el promedio dentro del composable. | Moví los promedios y el ahorro a una clase `ComparacionTiempos` en `model/Estados.kt`. La pantalla solo dibuja la tabla. Solo se guardan las cargas que terminan bien y agregué un botón «Borrar tiempos». | Calcular promedios es lógica y no debe ir en la vista. Si se guardaran los errores o los cortes por tiempo, el promedio no sería real. |

## Qué aprendí

- `withTimeout` cancela con una excepción especial; el orden de los `catch` importa.
- Avisar el progreso desde el Repository es fácil con una función, pero hay que cuidar
  cada cuánto se avisa para no frenar el cálculo.
- La IA tiende a poner cálculos dentro del composable porque "funciona"; hay que
  revisar en qué capa queda cada cosa.
