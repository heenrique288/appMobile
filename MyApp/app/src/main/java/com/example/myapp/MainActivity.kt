package com.example.myapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.room.Room
import com.example.myapp.data.*
import com.example.myapp.ui.theme.ReminderScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

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

        // ✅ Criação do banco de dados Room
        val db = Room.databaseBuilder(
            applicationContext,
            ReminderDatabase::class.java,
            "reminder_db"
        ).build()

        val dao = db.reminderDao()

        // ✅ Interface principal com Compose
        setContent {
            val reminders = remember { mutableStateListOf<Reminder>() }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                dao.getAllReminders().collectLatest {
                    reminders.clear()
                    reminders.addAll(it)
                }
            }

            ReminderScreen(
                reminders = reminders,
                onAdd = { scope.launch { dao.insert(it) } },
                onDelete = { scope.launch { dao.delete(it) } }
            )
        }
    }

    // ✅ Criação do canal de notificações
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
}
