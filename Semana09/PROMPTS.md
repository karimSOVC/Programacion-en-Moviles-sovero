# PROMPTS — MuroCampus (Fase 2 · rama MEJORA-IA)

Herramienta usada: **Claude Code**. Las seis mejoras respetan MVVM + Repository: el
ViewModel no tiene Retrofit, `PostApi`, `HttpException` ni `Context`, y la pantalla
nunca llama a la API.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Agrega un Interceptor de OkHttp que ponga `Accept: application/json` en todas las peticiones.» | Propuso un `Interceptor` que copia la petición con `newBuilder().header(...)` y lo agrega al `OkHttpClient`. | Lo agregué **antes** del `HttpLoggingInterceptor` y no lo puse en cada función de `PostApi` con `@Headers`. | Si va después del de registro, el encabezado no aparece en Logcat. Con un solo Interceptor, todas las peticiones lo llevan sin repetir código. |
| «Agrega un campo para buscar por título en las publicaciones ya descargadas.» | Propuso filtrar la lista dentro del Composable con `remember` y `filter`. | Descarté filtrar en la pantalla: el ViewModel tiene `busqueda` y `listaVisible`, que junta la lista y el texto con `combine`. Si no hay resultados dice «Sin resultados para…». | Filtrar es lógica y va en el ViewModel. Además, la búsqueda no vuelve a pedir nada a internet: trabaja con lo que ya se descargó. |
| «Carga las publicaciones de 20 en 20 con `_start` y `_limit` y un botón Cargar más.» | Propuso `@Query("_start")` y `@Query("_limit")` en una función nueva de `PostApi` y guardar la página actual en el ViewModel. | Calculo `_start` con el tamaño de la lista que ya está, uso `hayMas` (la página llegó llena) para ocultar el botón y `errorMas` para que un fallo no borre lo ya cargado. Con el filtro por usuario no se pagina, porque son solo 10. | Si «Cargar más» fallaba, la propuesta reemplazaba toda la lista por el error. Así se conservan las publicaciones y se puede reintentar. |
| «Agrega un botón Eliminar en el detalle con `@DELETE("posts/{id}")` que revise el código de respuesta con `Response<Unit>`.» | Propuso `suspend fun borrar(): Response<Unit>` y revisar `isSuccessful` en el ViewModel. | Moví la revisión del código al **Repository**: si no es exitoso lanza `HttpException(respuesta)` dentro de `llamar { }` y el ViewModel recibe un `Result<Int>` con el código. También quito la publicación de la lista que ya está en pantalla. | `Response` no lanza error con 404 o 500 y el ViewModel no puede conocer `Response` ni `HttpException` (regla de la guía). JSONPlaceholder no borra de verdad, por eso se quita en la app. |
| «Agrega una pantalla para modificar el título con PUT o PATCH y mostrar la respuesta.» | Propuso `@PUT("posts/{id}")` enviando el `Post` completo. | Usé **`@PATCH`** con una clase `CambioTitulo(title)`, porque solo cambia el título. La pantalla Editar muestra la respuesta del servidor (id, usuario y título) y actualiza el título en la lista y el detalle. | PUT reemplaza el recurso entero: habría que mandar también `body` y `userId`. PATCH manda solo lo que cambia, que es lo que pide la mejora. |
| «Guarda las publicaciones en Room y muéstralas sin internet; que el Repository decida si usa la red o la base de datos.» | Propuso una `@Entity`, un `@Dao` y que el ViewModel lea primero de Room y luego de la red. | Descarté que el ViewModel conozca Room: el `PostRepositoryRemote` recibe el DAO, guarda lo que descarga y, **solo si hay `IOException`** (sin internet), responde con lo guardado. Otros errores (404, JSON inesperado) se devuelven tal cual. El aviso «Sin internet: mostrando publicaciones guardadas» sale de `ultimaDesdeCache()`. | La guía pide que el Repository decida. Si la caché respondía también con un 404, la app escondería errores reales del servidor. El `Context` para crear la base se usa en `MainActivity`, nunca en el ViewModel. |

## Qué aprendí

- `Response<T>` sirve para leer el código HTTP, pero no lanza error con 404: hay que
  revisarlo uno mismo.
- PATCH cambia solo los campos que mandas; PUT reemplaza todo.
- La IA tiende a poner lógica en la pantalla o hacer que el ViewModel conozca la base de
  datos. Hay que revisar en qué capa queda cada cosa.
- Una caché debe usarse solo cuando falla la red, no para tapar cualquier error.
