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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
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
import androidx.compose.ui.text.style.TextOverflow
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
    viewModel: ReminderViewModel,
    onAdd: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit,
    onUpdate: (Reminder) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- ESTADOS PRINCIPAIS ---
    var title by remember { mutableStateOf(TextFieldValue("")) }
    val selectedDays = remember { mutableStateListOf<Int>() }
    var titleError by remember { mutableStateOf<String?>(null) }

    val maxChars = 30

    fun validateTitle(text: String) {
        titleError = when {
            text.isBlank() -> "O título não pode ser vazio"
            text.length > maxChars -> "Máximo de $maxChars caracteres"
            else -> null
        }
    }

    // botão só habilita se:
    // 1) título válido
    // 2) ao menos 1 dia selecionado
    val isButtonEnabled = titleError == null &&
            title.text.isNotBlank() &&
            selectedDays.isNotEmpty()

    // 🌈 Fundo gradiente
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

            // 🧾 CAMPO DE TÍTULO
            TextField(
                value = title,
                onValueChange = {
                    if (it.text.length <= maxChars) title = it
                    validateTitle(it.text)
                },
                label = { Text("Título do lembrete") },
                singleLine = true,
                isError = titleError != null,
                supportingText = {
                    if (titleError != null) {
                        Text(titleError!!, color = Color.Red)
                    } else {
                        Text("${title.text.length} / $maxChars")
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // CHIP DIAS DA SEMANA
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                val days = listOf("D", "S", "T", "Q", "Q", "S", "S")

                days.forEachIndexed { index, label ->
                    val selected = selectedDays.contains(index)

                    FilterChip(
                        selected = selected,
                        onClick = {
                            if (selected) selectedDays.remove(index)
                            else selectedDays.add(index)
                        },
                        label = {
                            Text(
                                label,
                                color = if (selected) Color.White else Color(0xFFEEEEEE)
                            )
                        }
                    )
                }
            }

            // ⏰ BOTÃO ADICIONAR LEMBRETE
            Button(
                onClick = {
                    val cal = Calendar.getInstance()

                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            val newReminder = Reminder(
                                title = title.text.trim(),
                                hour = hour,
                                minute = minute,
                                daysOfWeek = selectedDays.toList()
                            )

                            coroutineScope.launch {
                                val generatedId = viewModel.addReminderReturnId(newReminder)
                                val reminderWithId = newReminder.copy(id = generatedId.toInt())

                                NotificationUtils.scheduleNotification(context, reminderWithId)
                                onAdd(reminderWithId)
                            }

                            // limpar campos
                            title = TextFieldValue("")
                            titleError = null
                            selectedDays.clear()
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                enabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isButtonEnabled) Color(0xFF00E676) else Color(0xFF8BC34A),
                    disabledContainerColor = Color(0xFF8BC34A).copy(alpha = 0.3f)
                )
            ) {
                Text("Adicionar Lembrete", fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 🗂️ LISTA DE LEMBRETES
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reminders) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onDelete = onDelete,
                        onToggle = { updated ->
                            val toggled = updated.copy(isEnabled = !updated.isEnabled)
                            coroutineScope.launch {
                                onUpdate(toggled)
                                if (toggled.isEnabled)
                                    NotificationUtils.scheduleNotification(context, toggled)
                                else
                                    NotificationUtils.cancelNotification(context, updated)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onDelete: (Reminder) -> Unit,
    onToggle: (Reminder) -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1E1E1E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "⏰ %02d:%02d".format(reminder.hour, reminder.minute),
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                val daysMap = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
                Text(
                    text = reminder.daysOfWeek.joinToString(" - ") { daysMap[it] },
                    color = Color.DarkGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row {
                IconButton(onClick = { onToggle(reminder) }) {
                    Icon(
                        imageVector = if (reminder.isEnabled)
                            Icons.Default.NotificationsActive
                        else
                            Icons.Default.NotificationsOff,
                        contentDescription = "Ativar/Desativar",
                        tint = if (reminder.isEnabled) Color(0xFF00C853) else Color.Gray
                    )
                }

                IconButton(onClick = {
                    NotificationUtils.cancelNotification(context, reminder)
                    onDelete(reminder)
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir lembrete",
                        tint = Color(0xFFE53935)
                    )
                }
            }
        }
    }
}
