package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
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
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["customerId"]),
        Index(value = ["rentalId"])
    ]
)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val rentalId: Long? = null,
    val amount: Double,
    val paymentType: PaymentType,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val description: String = "",
    val receiptImage: String = "",
    val paymentDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class PaymentType {
    ADVANCE,
    RENTAL_PAYMENT,
    DAMAGE_PAYMENT,
    REFUND,
    OTHER
}

enum class PaymentMethod {
    CASH,
    BANK_TRANSFER,
    ONLINE,
    CHEQUE,
    OTHER
}