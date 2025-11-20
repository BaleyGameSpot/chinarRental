package com.chinarrental.app.data.dao

import androidx.room.*
import com.chinarrental.app.data.model.Reminder
import com.chinarrental.app.data.model.ReminderType
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY reminderTime ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun getReminderById(id: Long): Flow<Reminder?>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY reminderTime ASC")
    fun getPendingReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 1 ORDER BY reminderTime DESC")
    fun getCompletedReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE reminderTime >= :currentTime AND isCompleted = 0 ORDER BY reminderTime ASC")
    fun getUpcomingReminders(currentTime: Long = System.currentTimeMillis()): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE reminderTime < :currentTime AND isCompleted = 0 ORDER BY reminderTime DESC")
    fun getOverdueReminders(currentTime: Long = System.currentTimeMillis()): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE type = :type ORDER BY reminderTime ASC")
    fun getRemindersByType(type: ReminderType): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE customerId = :customerId ORDER BY reminderTime ASC")
    fun getRemindersByCustomer(customerId: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE reminderTime <= :currentTime AND isCompleted = 0 AND isSent = 0")
    fun getDueReminders(currentTime: Long = System.currentTimeMillis()): Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markAsCompleted(id: Long)

    @Query("UPDATE reminders SET isSent = 1 WHERE id = :id")
    suspend fun markAsSent(id: Long)
}