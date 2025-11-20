package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bills",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Rental::class,
            parentColumns = ["id"],
            childColumns = ["rentalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["customerId"]),
        Index(value = ["rentalId"])
    ]
)
data class Bill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val rentalId: Long,
    val billNumber: String,
    val billDate: Long = System.currentTimeMillis(),
    val amount: Double,
    val paidAmount: Double = 0.0,
    val status: BillStatus = BillStatus.UNPAID,
    val billImagePath: String = "",
    val pdfPath: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class BillStatus {
    PAID,
    UNPAID,
    PARTIALLY_PAID,
    CANCELLED
}