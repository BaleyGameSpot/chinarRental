package com.chinarrental.app.util

import com.chinarrental.app.data.model.Rental
import java.util.concurrent.TimeUnit

object RentalCalculator {

    /**
     * Calculate rental details based on dates and rates
     */
    fun calculateRental(
        startDate: Long,
        expectedReturnDate: Long,
        actualReturnDate: Long?,
        rentPerDay: Double,
        quantity: Int = 1,
        damageCharges: Double = 0.0,
        discountAmount: Double = 0.0
    ): RentalCalculation {

        // Calculate expected rental days
        val expectedDays = calculateDays(startDate, expectedReturnDate)

        // Calculate actual days if returned
        val actualDays = if (actualReturnDate != null) {
            calculateDays(startDate, actualReturnDate)
        } else {
            // If not returned yet, calculate from start to now
            calculateDays(startDate, System.currentTimeMillis())
        }

        // Calculate base rent
        val baseRent = expectedDays * rentPerDay * quantity

        // Calculate overdue days and rent
        val overdueDays = if (actualDays > expectedDays) actualDays - expectedDays else 0
        val overdueRent = overdueDays * rentPerDay * quantity * 1.5 // 50% extra for overdue

        // Calculate total
        val totalRent = baseRent + overdueRent
        val finalAmount = totalRent + damageCharges - discountAmount

        return RentalCalculation(
            expectedDays = expectedDays,
            actualDays = actualDays,
            overdueDays = overdueDays,
            baseRent = baseRent,
            overdueRent = overdueRent,
            totalRent = totalRent,
            damageCharges = damageCharges,
            discountAmount = discountAmount,
            finalAmount = finalAmount.coerceAtLeast(0.0)
        )
    }

    /**
     * Update rental with calculated values
     */
    fun updateRentalCalculation(
        rental: Rental,
        actualReturnDate: Long? = null,
        damageCharges: Double = rental.damageCharges,
        discountAmount: Double = rental.discountAmount,
        paidAmount: Double = rental.paidAmount
    ): Rental {
        val calculation = calculateRental(
            startDate = rental.startDate,
            expectedReturnDate = rental.expectedReturnDate,
            actualReturnDate = actualReturnDate,
            rentPerDay = rental.rentPerDay,
            quantity = rental.quantity,
            damageCharges = damageCharges,
            discountAmount = discountAmount
        )

        return rental.copy(
            actualReturnDate = actualReturnDate,
            totalRent = calculation.totalRent,
            overdueRent = calculation.overdueRent,
            damageCharges = damageCharges,
            discountAmount = discountAmount,
            finalAmount = calculation.finalAmount,
            paidAmount = paidAmount,
            remainingAmount = calculation.finalAmount - paidAmount,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Calculate days between two timestamps
     */
    private fun calculateDays(startTime: Long, endTime: Long): Int {
        val diff = endTime - startTime
        return TimeUnit.MILLISECONDS.toDays(diff).toInt().coerceAtLeast(1)
    }

    /**
     * Check if rental is overdue
     */
    fun isOverdue(expectedReturnDate: Long, actualReturnDate: Long? = null): Boolean {
        val checkDate = actualReturnDate ?: System.currentTimeMillis()
        return checkDate > expectedReturnDate
    }
}

data class RentalCalculation(
    val expectedDays: Int,
    val actualDays: Int,
    val overdueDays: Int,
    val baseRent: Double,
    val overdueRent: Double,
    val totalRent: Double,
    val damageCharges: Double,
    val discountAmount: Double,
    val finalAmount: Double
)
