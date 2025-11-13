package com.example.myapp.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.myapp.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Lembrete"

        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lembrete diário: $title") // ✅ mostra o nome do lembrete junto
            .setContentText("Não se esqueça de $title!") // ✅ mensagem mais amigável
            .setStyle(NotificationCompat.BigTextStyle().bigText("Não se esqueça de $title!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // ✅ Verifica permissão antes de notificar
        val notificationManager = NotificationManagerCompat.from(context)
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } else {
            // Opcional: log ou tratamento silencioso
            println("Permissão de notificação não concedida.")
        }
    }
}
