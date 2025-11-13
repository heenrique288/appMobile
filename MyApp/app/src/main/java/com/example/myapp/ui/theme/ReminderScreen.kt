package com.example.myapp.ui.theme

import android.app.TimePickerDialog
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.data.Reminder
import com.example.myapp.notification.NotificationUtils
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    reminders: List<Reminder>,
    onAdd: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf(TextFieldValue("")) }

    // 🌈 Fundo gradiente suave
    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFF6A11CB),
            Color(0xFF2575FC)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "✨ Lembretes Diários",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = "Adicione e gerencie seus lembretes pessoais",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
            )

            // 🧾 Campo de título
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título do lembrete") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                        modifier = Modifier.fillMaxWidth()
            )

            // ⏰ Botão para escolher hora e salvar lembrete
            Button(
                onClick = {
                    val cal = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            val reminder = Reminder(title = title.text, hour = hour, minute = minute)
                            coroutineScope.launch {
                                onAdd(reminder)
                                NotificationUtils.scheduleNotification(context, title.text, hour, minute)
                            }
                            title = TextFieldValue("")
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
            ) {
                Text("Adicionar Lembrete", fontWeight = FontWeight.Bold)
            }

            Divider(
                color = Color.White.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 🗂️ Lista de lembretes
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reminders) { reminder ->
                    ReminderCard(reminder = reminder, onDelete = onDelete)
                }
            }
        }
    }
}

@Composable
fun ReminderCard(reminder: Reminder, onDelete: (Reminder) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E1E1E)
                )
                Text(
                    text = "⏰ %02d:%02d".format(reminder.hour, reminder.minute),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            IconButton(onClick = { onDelete(reminder) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir lembrete",
                    tint = Color(0xFFE53935)
                )
            }
        }
    }
}
