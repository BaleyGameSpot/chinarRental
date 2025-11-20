package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val category: TransactionCategory,
    val amount: Double,
    val description: String = "",
    val referenceId: Long? = null,
    val referenceType: String = "",
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val date: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE,
    LOAN_GIVEN,
    LOAN_RECEIVED,
    ADVANCE_RECEIVED,
    ADVANCE_GIVEN
}

enum class TransactionCategory {
    RENTAL_INCOME,
    MAINTENANCE,
    UTILITIES,
    SALARIES,
    RENT_EXPENSE,
    SUPPLIES,
    REPAIRS,
    MARKETING,
    OTHER_INCOME,
    OTHER_EXPENSE
}
