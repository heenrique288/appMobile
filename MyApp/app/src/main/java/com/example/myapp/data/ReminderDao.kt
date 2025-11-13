package com.example.myapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY hour, minute")
    fun getAllReminders(): Flow<List<Reminder>>

    @Insert
    suspend fun insert(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)
}
