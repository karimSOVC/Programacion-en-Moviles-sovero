# CentroTareas

App de Android (Kotlin + Jetpack Compose + Material 3) que hace tres tareas que tardan
sin congelar la pantalla: una tarea con progreso, contar números primos y dos cargas
de datos simuladas. Laboratorio 14 · Hilos en Android.

## Observación del Paso 1

**Qué pasa con la pantalla:** al tocar **Tarea larga (MAL)** la app se queda quieta
durante 4 segundos. Si toco **Tócame** en ese tiempo, el contador no sube y el botón no
reacciona. Cuando pasan los 4 segundos, el texto cambia directo a **Terminó** y recién
ahí se ven los toques que hice.

**Por qué no se ve «Trabajando...»:** `Thread.sleep(4000)` se ejecuta en el hilo
principal, que es el mismo que dibuja la pantalla. El mensaje sí cambia a
«Trabajando...», pero el hilo está dormido y no puede volver a dibujar. Cuando despierta,
el mensaje ya es «Terminó», así que solo se dibuja ese.

**Si la espera fuera de 10 segundos:** Android mostraría el aviso **«La aplicación no
responde» (ANR)**, con la opción de cerrarla o esperar. Esto pasa cuando el hilo
principal no atiende un toque durante unos 5 segundos.
