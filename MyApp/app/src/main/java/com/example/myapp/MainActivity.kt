package com.example.myapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapp.data.ReminderDatabase
import com.example.myapp.data.ReminderRepository
import com.example.myapp.ui.theme.ReminderScreen
import com.example.myapp.ui.theme.ReminderViewModel
import androidx.compose.runtime.collectAsState
import com.example.myapp.ui.theme.ReminderViewModelFactory
import androidx.lifecycle.lifecycleScope
import com.example.myapp.data.Reminder
import com.example.myapp.notification.NotificationUtils
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        requestNotificationPermission()

        // ✅ Instância do banco e repositório
        val dao = ReminderDatabase.getDatabase(applicationContext).reminderDao()
        val repository = ReminderRepository(dao)
        val factory = ReminderViewModelFactory(repository)
        val viewModel: ReminderViewModel by viewModels { factory }

        // ✅ Interface com Compose
        setContent {
            val reminders = viewModel.reminders.collectAsState(initial = emptyList())

            ReminderScreen(
                reminders = reminders.value,
                viewModel = viewModel,  // <-- AGORA ELE EXISTE NO COMPOSABLE
                onAdd = {},
                onDelete = { viewModel.deleteReminder(it) },
                onUpdate = { viewModel.updateReminder(it) }
            )
        }
        lifecycleScope.launch {
            viewModel.reminders.collect { list ->
                // cancelar possíveis alarms antigos com id = 0
                val fakeZeroReminder =
                    Reminder(id = 0, title = "", hour = 0, minute = 0, daysOfWeek = (0..6).toList())
                NotificationUtils.cancelNotification(applicationContext, fakeZeroReminder)

                // reagendar todos os reminders ativos com id correto
                list.forEach { r ->
                    if (r.isEnabled) {
                        NotificationUtils.cancelNotification(applicationContext, r) // evita duplicados
                        NotificationUtils.scheduleNotification(applicationContext, r)
                    }
                }
            }
        }
    }

    // 🔔 Canal de notificações
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Lembretes Diários",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    // 📱 Solicita permissão de notificação (Android 13+)
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}
