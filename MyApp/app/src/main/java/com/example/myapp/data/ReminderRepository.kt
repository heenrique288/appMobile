package com.example.myapp.data

class ReminderRepository(private val dao: ReminderDao) {

    val reminders = dao.getAllReminders()

    suspend fun add(reminder: Reminder) = dao.insert(reminder)

    suspend fun delete(reminder: Reminder) = dao.delete(reminder)

    suspend fun update(reminder: Reminder) = dao.update(reminder)
}
