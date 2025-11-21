package com.example.myapp.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapp.data.Reminder
import com.example.myapp.data.ReminderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(private val repository: ReminderRepository) : ViewModel() {

    // Flow contendo todos os lembretes
    val reminders = repository.reminders
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Inserção SEM retornar ID
    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.add(reminder)
        }
    }

    // Inserção que retorna o ID do Room
    suspend fun addReminderReturnId(reminder: Reminder): Long {
        return repository.addReturnId(reminder)
    }

    // Atualizar lembrete
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.update(reminder)
        }
    }

    // Apagar lembrete
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.delete(reminder)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ReminderViewModelFactory(
    private val repository: ReminderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            return ReminderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
