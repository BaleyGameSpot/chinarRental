package com.chinarrental.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chinarrental.app.data.dao.ReminderDao
import com.chinarrental.app.data.model.Reminder
import com.chinarrental.app.data.model.ReminderType
import com.chinarrental.app.data.model.RentalStatus
import com.chinarrental.app.data.repository.RentalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val rentalRepository: RentalRepository,
    private val reminderDao: ReminderDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            checkOverdueRentals()
            checkOverduePayments()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun checkOverdueRentals() {
        val currentTime = System.currentTimeMillis()
        val activeRentals = rentalRepository.getRentalsByStatus(RentalStatus.ACTIVE).first()

        for (rental in activeRentals) {
            // Check if rental is overdue (return date passed)
            if (rental.expectedReturnDate < currentTime) {
                val daysOverdue = ((currentTime - rental.expectedReturnDate) / (1000 * 60 * 60 * 24)).toInt()

                // Check if reminder already exists for this rental
                val existingReminders = reminderDao.getRemindersByRental(rental.id).first()
                val hasOverdueReminder = existingReminders.any {
                    it.type == ReminderType.OVERDUE && !it.isCompleted
                }

                if (!hasOverdueReminder) {
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    val expectedDate = dateFormat.format(Date(rental.expectedReturnDate))

                    val reminder = Reminder(
                        title = "OVERDUE: Rental #${rental.id}",
                        message = "Item is overdue by $daysOverdue day(s). Expected return: $expectedDate",
                        reminderTime = currentTime,
                        type = ReminderType.OVERDUE,
                        referenceId = rental.id,
                        customerId = rental.customerId,
                        sendNotification = true,
                        sendSms = true,
                        isSent = false,
                        isCompleted = false
                    )
                    reminderDao.insertReminder(reminder)
                }
            }
        }
    }

    private suspend fun checkOverduePayments() {
        val currentTime = System.currentTimeMillis()
        val rentalsWithPendingPayment = rentalRepository.getRentalsWithPendingPayment().first()

        for (rental in rentalsWithPendingPayment) {
            if (rental.remainingAmount > 0) {
                // Check if payment reminder already exists
                val existingReminders = reminderDao.getRemindersByRental(rental.id).first()
                val hasPaymentReminder = existingReminders.any {
                    it.type == ReminderType.PAYMENT_DUE && !it.isCompleted
                }

                if (!hasPaymentReminder) {
                    val reminder = Reminder(
                        title = "Payment Due: Rental #${rental.id}",
                        message = "Pending payment amount: PKR ${String.format("%.2f", rental.remainingAmount)}",
                        reminderTime = currentTime,
                        type = ReminderType.PAYMENT_DUE,
                        referenceId = rental.id,
                        customerId = rental.customerId,
                        sendNotification = true,
                        sendSms = false,
                        isSent = false,
                        isCompleted = false
                    )
                    reminderDao.insertReminder(reminder)
                }
            }
        }
    }
}
