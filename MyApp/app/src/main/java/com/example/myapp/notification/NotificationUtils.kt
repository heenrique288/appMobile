package com.example.myapp.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.myapp.data.Reminder
import java.util.*

object NotificationUtils {

    private fun getRequestCode(reminder: Reminder, day: Int): Int {
        return reminder.id * 100 + day
    }

    private fun buildIntent(context: Context, reminder: Reminder, day: Int): Intent {
        return Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("id", reminder.id)
            putExtra("hour", reminder.hour)
            putExtra("minute", reminder.minute)
            putExtra("day", day)
        }
    }

    fun scheduleNotification(context: Context, reminder: Reminder) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        val nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        reminder.daysOfWeek.forEach { day ->

            val requestCode = getRequestCode(reminder, day)
            val intent = buildIntent(context, reminder, day)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, reminder.hour)
                set(Calendar.MINUTE, reminder.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val targetDay = day + 1
            var daysToAdd = targetDay - nowDay
            if (daysToAdd < 0) daysToAdd += 7
            if (daysToAdd == 0 && calendar.timeInMillis <= System.currentTimeMillis())
                daysToAdd = 7

            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelNotification(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        reminder.daysOfWeek.forEach { day ->

            val requestCode = getRequestCode(reminder, day)
            val intent = buildIntent(context, reminder, day)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }
}
