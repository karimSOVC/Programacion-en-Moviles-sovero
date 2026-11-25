package com.sovero.avisoscampus.notificacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sovero.avisoscampus.MainActivity
import com.sovero.avisoscampus.R
import com.sovero.avisoscampus.model.Aviso

/**
 * Todo lo que toca el sistema de notificaciones de Android: el canal,
 * el permiso y mostrar el aviso. Lo usa la pantalla, nunca el ViewModel.
 */
object Notificador {
    const val CANAL = "avisos"

    // Mejora (Fase 2): segundo canal, con importancia alta
    const val CANAL_URGENTE = "urgentes"

    /**
     * Crea los canales "Avisos" y "Urgentes". Desde Android 8 (API 26, O)
     * toda notificación necesita un canal; en versiones anteriores no existen.
     * Crearlos varias veces no pasa nada: Android los crea una sola vez.
     */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL,
                "Avisos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Mejora (Fase 2): IMPORTANCE_HIGH hace que salga como ventana
            // emergente arriba de la pantalla y con sonido
            val urgente = NotificationChannel(
                CANAL_URGENTE,
                "Urgentes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Recordatorios que no hay que perder" }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
            manager.createNotificationChannel(urgente)
        }
    }

    /**
     * true si la app puede mostrar notificaciones. Antes de Android 13
     * (API 33) no hay que pedir nada; desde Android 13 se revisa el permiso.
     */
    fun tienePermiso(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Paso 3: muestra el aviso en el panel de notificaciones.
     * Devuelve false (sin error) si no hay permiso, y true si se mostró.
     */
    // El permiso ya se revisa al inicio con tienePermiso
    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, aviso: Aviso): Boolean {
        if (!tienePermiso(context)) return false

        // Paso 4: al tocar la notificación se abre MainActivity.
        // FLAG_IMMUTABLE: nadie puede cambiar este Intent después
        val intent = Intent(context, MainActivity::class.java)
        val pendiente = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Mejora (Fase 2): el aviso urgente va por el canal Urgentes. La
        // prioridad alta es para Android 7 o menos, donde no hay canales
        val canal = if (aviso.urgente) CANAL_URGENTE else CANAL
        val prioridad = if (aviso.urgente) {
            NotificationCompat.PRIORITY_HIGH
        } else {
            NotificationCompat.PRIORITY_DEFAULT
        }

        // Mejora (Fase 2): texto que se ve al expandir la notificación.
        // Cerrada muestra una sola línea; abierta, el texto completo
        val textoLargo = "${aviso.texto}.\nOrigen: ${aviso.origen.texto} · Hora: ${aviso.hora}" +
            "\nToca la notificación para abrir AvisosCampus y ver el historial."

        val notificacion = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(textoLargo))
            .setPriority(prioridad)
            .setContentIntent(pendiente)
            // Al tocarla, la notificación se quita del panel
            .setAutoCancel(true)
            .build()

        // El id del aviso hace que cada notificación sea distinta
        NotificationManagerCompat.from(context).notify(aviso.id, notificacion)
        return true
    }
}
