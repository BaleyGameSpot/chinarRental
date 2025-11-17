package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "rentals",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Rental(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val itemId: Long,
    val quantity: Int = 1,
    val startDate: Long,
    val expectedReturnDate: Long,
    val actualReturnDate: Long? = null,
    val rentPerDay: Double,
    val totalRent: Double = 0.0,
    val overdueRent: Double = 0.0,
    val damageCharges: Double = 0.0,
    val discountAmount: Double = 0.0,
    val finalAmount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val status: RentalStatus = RentalStatus.ACTIVE,
    val pickupLocation: String = "",
    val pickupLatitude: Double? = null,
    val pickupLongitude: Double? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class RentalStatus {
    ACTIVE,
    RETURNED,
    OVERDUE,
    CANCELLED
}
