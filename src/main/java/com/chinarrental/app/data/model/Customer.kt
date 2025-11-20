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
    val cnic: String = "", // Alias for cnicNumber
    val cnicNumber: String = "", // Keep for compatibility
    val cnicPhotoFront: String = "",
    val cnicPhotoBack: String = "",
    val cnicFrontImageUri: String = "", // For ViewModel compatibility
    val cnicBackImageUri: String = "", // For ViewModel compatibility
    val latitude: Double? = null,
    val longitude: Double? = null,
    val location: String = "", // Alias for locationName
    val locationName: String = "",
    val hasDiscount: Boolean = false,
    val discountType: DiscountType = DiscountType.NONE,
    val discountValue: Double = 0.0,
    val discount: Double = 0.0, // Alias for discountValue
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class DiscountType {
    NONE,
    PERCENTAGE,
    FLAT_AMOUNT
}