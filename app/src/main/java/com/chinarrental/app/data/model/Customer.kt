package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val phone2: String = "",
    val email: String = "",
    val address: String = "",
    val cnicNumber: String = "",
    val cnicPhotoFront: String = "",
    val cnicPhotoBack: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String = "",
    val hasDiscount: Boolean = false,
    val discountType: DiscountType = DiscountType.NONE,
    val discountValue: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DiscountType {
    NONE,
    PERCENTAGE,
    FLAT_AMOUNT
}
