package com.example.myapp.ui.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.notification.NotificationHelper
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen() {

    // Gradiente igual ao ReminderScreen
    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFF6A11CB),
            Color(0xFF2575FC)
        )
    )

    var totalTime by rememberSaveable { mutableStateOf(0L) }
    var remainingTime by rememberSaveable { mutableStateOf(0L) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var hasStarted by rememberSaveable { mutableStateOf(false) }

    var minutesInput by rememberSaveable { mutableStateOf("") }
    var secondsInput by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current


    // TIMER LOOP
    LaunchedEffect(isRunning) {
        while (isRunning && remainingTime > 0) {
            delay(100)
            remainingTime -= 100

            if (remainingTime <= 0) {
                isRunning = false
                remainingTime = 0
                NotificationHelper.showTimerFinished(context)
            }
        }
    }

    fun format(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", minutes, sec)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)         // ← gradiente aplicado
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = format(remainingTime),
                fontSize = 48.sp,
                color = Color.White
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = minutesInput,
                    onValueChange = {
                        if (!isRunning && it.length <= 2 && it.all { c -> c.isDigit() })
                            minutesInput = it
                    },
                    label = { Text("Minutos") },
                    modifier = Modifier.width(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.6f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                    )
                )

                OutlinedTextField(
                    value = secondsInput,
                    onValueChange = {
                        if (!isRunning && it.length <= 2 && it.all { c -> c.isDigit() })
                            secondsInput = it
                    },
                    label = { Text("Segundos") },
                    modifier = Modifier.width(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.6f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val startLabel = if (isRunning) "Pausar" else "Iniciar"
                val startColor = if (isRunning) Color(0xFFE53935) else Color(0xFF00C853)

                Button(
                    onClick = {
                        if (isRunning) {
                            isRunning = false
                        } else {
                            val m = minutesInput.toIntOrNull() ?: 0
                            val s = secondsInput.toIntOrNull() ?: 0

                            totalTime = (m * 60 + s) * 1000L
                            if (totalTime > 0) {
                                remainingTime = totalTime
                                hasStarted = true
                                isRunning = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = startColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .width(140.dp)
                ) {
                    Text(startLabel, color = Color.White, fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        isRunning = false
                        hasStarted = false
                        remainingTime = 0L
                        minutesInput = ""
                        secondsInput = ""
                    },
                    enabled = hasStarted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasStarted) Color(0xFFBDBDBD) else Color(0xFFEEEEEE),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(56.dp)
                        .width(140.dp)
                ) {
                    Text("Cancelar", color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }
}
