# PROMPTS — AvisosCampus (Fase 2 · rama MEJORA-IA)

Herramienta usada: **Claude Code**. Las tres mejoras respetan MVVM + Repository: el
ViewModel no tiene `Context`, `NotificationManager` ni `BroadcastReceiver`, y todo
receptor se quita en `onDispose`.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Agrega un canal Urgentes con IMPORTANCE_HIGH y otro Avisos; el recordatorio debe usar el urgente.» | Propuso crear el segundo canal en `crearCanal` y, en `mostrar`, elegir el canal según `aviso.origen == Origen.RECORDATORIO`. | Descarté que el `Notificador` decida por el origen. Agregué `urgente` al `Aviso` y al `registrar` del Repository, y es el **ViewModel** quien marca el recordatorio como urgente. El `Notificador` solo lee esa bandera. También puse `PRIORITY_HIGH` para Android 7 o menos. | Decidir qué es urgente es una regla de la app y va en el ViewModel; el `Notificador` solo debe saber mostrar. Si mañana otro aviso es urgente, se cambia en un solo lugar. |
| «Muestra el texto completo al expandir la notificación con BigTextStyle.» | Propuso `setStyle(NotificationCompat.BigTextStyle().bigText(aviso.texto))`. | Como los textos son cortos, al expandir no se veía ninguna diferencia. Armé un texto largo con el texto, el origen, la hora y una indicación para abrir la app. Cerrada, la notificación sigue mostrando una línea. | `BigTextStyle` solo sirve si el texto expandido trae más información que la línea normal; si no, la mejora no se nota. |
| «Escucha ACTION_POWER_CONNECTED y ACTION_POWER_DISCONNECTED y avisa, con su propio switch.» | Propuso registrar el receptor en el `AndroidManifest.xml` y desde ahí llamar al ViewModel. | Descarté el Manifest: lo registré en la pantalla con `DisposableEffect`, igual que el del modo avión, con `RECEIVER_NOT_EXPORTED` y `unregisterReceiver` en `onDispose`. Agregué `Origen.CARGADOR` con su color en el historial. | Un receptor del Manifest no tiene acceso al ViewModel, y desde Android 8 casi no recibe estos anuncios. Registrado en la pantalla solo escucha con el switch activado y no queda filtrando memoria. |

## Qué aprendí

- La importancia de una notificación la decide el **canal**: con `IMPORTANCE_HIGH` sale
  como ventana emergente.
- `BigTextStyle` sirve cuando el texto expandido aporta más que la línea corta.
- Todo `BroadcastReceiver` que se registra en la pantalla se tiene que quitar en
  `onDispose`.
- La IA tiende a poner decisiones de la app en el `Notificador` o en el Manifest; hay que
  revisar en qué capa queda cada cosa.
