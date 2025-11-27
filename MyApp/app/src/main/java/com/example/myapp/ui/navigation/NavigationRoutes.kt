package com.example.myapp.ui.navigation

sealed class Screen(val route: String) {
    object Reminders : Screen("reminders")
    object Timer : Screen("timer")
    object Chronometer : Screen("chronometer")
}
