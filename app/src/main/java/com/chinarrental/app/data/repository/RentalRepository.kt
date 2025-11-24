package com.chinarrental.app.data.repository

import com.chinarrental.app.data.dao.*
import com.chinarrental.app.data.model.*
import com.chinarrental.app.util.RentalCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RentalRepository @Inject constructor(
    private val rentalDao: RentalDao,
    private val itemDao: ItemDao,
    private val customerDao: CustomerDao,
    private val paymentDao: PaymentDao,
    private val reminderDao: ReminderDao,
    private val billDao: BillDao
) {

    fun getAllRentals(): Flow<List<Rental>> = rentalDao.getAllRentals()

    fun getRentalById(id: Long): Flow<Rental?> = rentalDao.getRentalById(id)

    fun getRentalsByCustomer(customerId: Long): Flow<List<Rental>> =
        rentalDao.getRentalsByCustomer(customerId)

    fun getRentalsByItem(itemId: Long): Flow<List<Rental>> =
        rentalDao.getRentalsByItem(itemId)

    fun getRentalsByStatus(status: RentalStatus): Flow<List<Rental>> =
        rentalDao.getRentalsByStatus(status)

    fun getOverdueRentals(): Flow<List<Rental>> = rentalDao.getOverdueRentals()

    fun getRentalsWithPendingPayment(): Flow<List<Rental>> =
        rentalDao.getRentalsWithPendingPayment()

    suspend fun createRental(rental: Rental): Result<Long> {
        return try {
            // Check item availability
            val item = itemDao.getItemById(rental.itemId).first()
            if (item == null) {
                return Result.failure(Exception("Item not found"))
            }
            if (item.availableQuantity < rental.quantity) {
                return Result.failure(Exception("Insufficient quantity available"))
            }

            // Calculate rental amounts
            val calculatedRental = RentalCalculator.updateRentalCalculation(rental)

            // Insert rental
            val rentalId = rentalDao.insertRental(calculatedRental)

            // Update item availability
            itemDao.decreaseAvailableQuantity(rental.itemId, rental.quantity)

            // Create reminder for return date
            val reminder = Reminder(
                title = "Return Reminder",
                message = "Item rental return due today",
                reminderTime = rental.expectedReturnDate,
                type = ReminderType.RETURN_DATE,
                referenceId = rentalId,
                customerId = rental.customerId,
                sendNotification = true,
                sendSms = true
            )
            reminderDao.insertReminder(reminder)

            // Generate bill for the rental
            val billNumber = "BILL-${System.currentTimeMillis()}"
            val bill = Bill(
                customerId = rental.customerId,
                rentalId = rentalId,
                billNumber = billNumber,
                billDate = System.currentTimeMillis(),
                amount = calculatedRental.finalAmount,
                paidAmount = calculatedRental.paidAmount,
                status = if (calculatedRental.paidAmount >= calculatedRental.finalAmount) {
                    BillStatus.PAID
                } else if (calculatedRental.paidAmount > 0) {
                    BillStatus.PARTIALLY_PAID
                } else {
                    BillStatus.UNPAID
                },
                notes = "Rental bill for item: ${item.name}"
            )
            billDao.insertBill(bill)

            Result.success(rentalId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRental(rental: Rental): Result<Unit> {
        return try {
            val updatedRental = RentalCalculator.updateRentalCalculation(
                rental = rental,
                actualReturnDate = rental.actualReturnDate,
                damageCharges = rental.damageCharges,
                discountAmount = rental.discountAmount,
                paidAmount = rental.paidAmount
            )
            rentalDao.updateRental(updatedRental)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun returnRental(
        rentalId: Long,
        returnDate: Long,
        damageCharges: Double = 0.0
    ): Result<Unit> {
        return try {
            val rental = rentalDao.getRentalById(rentalId).first()
                ?: return Result.failure(Exception("Rental not found"))

            val updatedRental = RentalCalculator.updateRentalCalculation(
                rental = rental,
                actualReturnDate = returnDate,
                damageCharges = damageCharges,
                discountAmount = rental.discountAmount,
                paidAmount = rental.paidAmount
            ).copy(status = RentalStatus.RETURNED)

            rentalDao.updateRental(updatedRental)

            // Increase item availability
            itemDao.increaseAvailableQuantity(rental.itemId, rental.quantity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addPayment(rentalId: Long, amount: Double): Result<Unit> {
        return try {
            val rental = rentalDao.getRentalById(rentalId).first()
                ?: return Result.failure(Exception("Rental not found"))

            val payment = Payment(
                customerId = rental.customerId,
                rentalId = rentalId,
                amount = amount,
                paymentType = PaymentType.RENTAL_PAYMENT
            )
            paymentDao.insertPayment(payment)

            val updatedRental = rental.copy(
                paidAmount = rental.paidAmount + amount,
                remainingAmount = rental.remainingAmount - amount
            )
            rentalDao.updateRental(updatedRental)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRental(rental: Rental): Result<Unit> {
        return try {
            // Return item quantity
            itemDao.increaseAvailableQuantity(rental.itemId, rental.quantity)
            rentalDao.deleteRental(rental)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
