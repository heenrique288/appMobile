package com.example.myapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    // Retorna lista de lembretes em Flow
    @Query("SELECT * FROM reminders ORDER BY hour, minute")
    fun getAllReminders(): Flow<List<Reminder>>

    // Inserção padrão já retorna o ID gerado pelo Room
    @Insert
    suspend fun insert(reminder: Reminder): Long

    // Alias opcional para inserir especificamente retornando ID
    @Insert
    suspend fun insertReturnId(reminder: Reminder): Long

    // Apagar lembrete
    @Delete
    suspend fun delete(reminder: Reminder)

    // Atualizar lembrete
    @Update
    suspend fun update(reminder: Reminder)
}
