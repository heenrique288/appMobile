package com.example.myapp.data

class ReminderRepository(private val dao: ReminderDao) {

    // Flow com todos os lembretes
    val reminders = dao.getAllReminders()

    // Inserção simples (não retorna ID)
    suspend fun add(reminder: Reminder) {
        dao.insert(reminder)
    }

    // Inserção que retorna o ID do Room
    suspend fun addReturnId(reminder: Reminder): Long {
        return dao.insertReturnId(reminder)
    }

    // Atualizar lembrete
    suspend fun update(reminder: Reminder) {
        dao.update(reminder)
    }

    // Apagar lembrete
    suspend fun delete(reminder: Reminder) {
        dao.delete(reminder)
    }
}
