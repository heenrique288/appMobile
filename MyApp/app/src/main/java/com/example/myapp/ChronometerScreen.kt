package com.example.myapp.ui.chronometer

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ChronometerScreen() {

    // Gradient igual ao ReminderScreen
    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFF6A11CB),
            Color(0xFF2575FC)
        )
    )

    // elapsed in milliseconds
    var elapsedMillis by rememberSaveable { mutableStateOf(0L) }
    var isRunning by rememberSaveable { mutableStateOf(false) }
    var hasStarted by rememberSaveable { mutableStateOf(false) }

    var startTime by remember { mutableStateOf(0L) }

    // Loop do cronômetro
    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTime = System.currentTimeMillis() - elapsedMillis
            while (isRunning) {
                elapsedMillis = System.currentTimeMillis() - startTime
                delay(50)
            }
        }
    }

    fun formatElapsed(ms: Long): String {
        val centis = (ms / 10 % 100).toInt()
        val seconds = (ms / 1000 % 60).toInt()
        val minutes = (ms / 60000).toInt()
        return String.format("%02d:%02d.%02d", minutes, seconds, centis)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)    // ← gradiente aplicado aqui
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            Text(
                text = formatElapsed(elapsedMillis),
                fontSize = 48.sp,
                color = Color.White
            )

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
                            hasStarted = true
                            isRunning = true
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
                        elapsedMillis = 0L
                        hasStarted = false
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
