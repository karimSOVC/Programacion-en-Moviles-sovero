# Lab03 - Registro de Producto

**Alumno:** Karim Sovero
**Curso:** Programación en Móviles - Semana 03

## Descripción

Aplicación Android hecha con Jetpack Compose que permite registrar un producto.
El usuario ingresa nombre, precio y cantidad; al presionar AGREGAR PRODUCTO
aparece una Card con el resumen y el importe calculado (precio × cantidad) con
2 decimales.

## Capturas

Pantalla inicial (formulario vacío):

![Pantalla inicial](captura1.png)

Después de presionar AGREGAR PRODUCTO:

![Producto registrado](captura2.png)

## ¿Qué pasaría si declaras las variables de los campos SIN remember?

Al quitar el remember de la variable nombre y el campo no permite
escribir nada, se queda vacío. Sin remember, la variable se vuelve a crear
en cada recomposición, así que el valor que escribo se pierde de inmediato.
remember conserva el valor entre recomposiciones.

## Mejora con IA

| Prompt que usé | Qué generó Gemini | Qué acepté o corregí (y por qué) |
|---|---|---|
| Tengo una pantalla en Jetpack Compose llamada PantallaRegistro con tres OutlinedTextField (nombre, precio, cantidad), un botón AGREGAR PRODUCTO y una Card que muestra el resumen con el importe. Necesito que le agregues dos cosas dentro de PantallaRegistro: (1) Validación de campos vacíos que al presionar AGREGAR PRODUCTO, si alguno de los tres campos está vacío, que muestre un mensaje de error en rojo en lugar de la card. (2) Un botón para limpiar que vacíe los tres campos y oculte la Card. No toques MainActivity, ni el tema, ni el cálculo del importe, ni los textos del encabezado. Pásame solo la función PantallaRegistro completa | Agregó un estado `errorMensaje`, validación con `isBlank()` en el onClick del botón AGREGAR, un botón LIMPIAR que vacía los campos y oculta la Card, y un `if/else if` que muestra el error en rojo o el resumen | Acepté la validación y el botón Limpiar porque funcionaban bien. Corregí tres cosas: (1) la Card leía directo de los campos, así que al editar el precio después de agregar el resumen cambiaba solo; agregué estados guardados para que mantenga los datos del momento del registro. (2) Precio y cantidad aceptaban letras; agregué un filtro para que solo permitan números. (3) El espaciado entre botones era de 8.dp y lo pasé a 16.dp según las reglas de diseño del laboratorio |