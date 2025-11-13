package com.example.myapp.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build    // ✅ importa o Build
import android.util.Log
import java.util.*

object NotificationUtils {

    fun scheduleNotification(context: Context, title: String, hour: Int, minute: Int) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            title.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // ✅ calcula o horário exato em milissegundos
        var triggerTime = calendar.timeInMillis

        // se o horário já passou hoje, agenda para amanhã
        if (triggerTime < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            triggerTime = calendar.timeInMillis
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // ✅ apenas chama se a API permitir (evita crash)
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                Log.w("NotificationUtils", "Permissão de alarme exato não concedida.")
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }

        Log.d("NotificationUtils", "Lembrete agendado para $hour:$minute — $title")
    }
}
