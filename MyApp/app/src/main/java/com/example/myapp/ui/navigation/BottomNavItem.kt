package com.example.myapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Reminders : BottomNavItem("reminders", "Lembretes", Icons.Default.Notifications)
    object Timer : BottomNavItem("timer", "Temporizador", Icons.Default.Timer)
    object Chronometer : BottomNavItem("chronometer", "Cronômetro", Icons.Default.AccessTime)
}
