package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.ReminderDao
import com.chinarrental.app.data.model.Reminder
import com.chinarrental.app.data.model.ReminderType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val reminderDao: ReminderDao
) {

    fun getAllReminders(): Flow<List<Reminder>> = reminderDao.getAllReminders()

    fun getReminderById(id: Long): Flow<Reminder?> = reminderDao.getReminderById(id)

    fun getRemindersByType(type: ReminderType): Flow<List<Reminder>> =
        reminderDao.getRemindersByType(type)

    fun getRemindersByCustomer(customerId: Long): Flow<List<Reminder>> =
        reminderDao.getRemindersByCustomer(customerId)

    fun getPendingReminders(): Flow<List<Reminder>> = reminderDao.getPendingReminders()

    fun getCompletedReminders(): Flow<List<Reminder>> = reminderDao.getCompletedReminders()

    fun getUpcomingReminders(): Flow<List<Reminder>> = reminderDao.getUpcomingReminders()

    fun getOverdueReminders(): Flow<List<Reminder>> = reminderDao.getOverdueReminders()

    suspend fun insertReminder(reminder: Reminder): Result<Long> {
        return try {
            val id = reminderDao.insertReminder(reminder)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReminder(reminder: Reminder): Result<Unit> {
        return try {
            reminderDao.updateReminder(reminder)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReminder(reminder: Reminder): Result<Unit> {
        return try {
            reminderDao.deleteReminder(reminder)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsCompleted(reminderId: Long): Result<Unit> {
        return try {
            reminderDao.markAsCompleted(reminderId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
