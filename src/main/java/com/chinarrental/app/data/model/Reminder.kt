package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val reminderTime: Long,
    val type: ReminderType,
    val referenceId: Long? = null,
    val customerId: Long? = null,
    val customerPhone: String = "",
    val isCompleted: Boolean = false,
    val isSent: Boolean = false,
    val sendSms: Boolean = false,
    val sendNotification: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ReminderType {
    RETURN_DATE,
    PAYMENT_DUE,
    LOW_STOCK,
    CUSTOM,
    OVERDUE
}
