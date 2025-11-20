package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Will store ItemCategory.name
    val rentPerDay: Double,
    val quantity: Int,
    val totalQuantity: Int = quantity, // Alias for quantity
    val availableQuantity: Int,
    val description: String = "",
    val imageUrl: String = "",
    val imageUri: String = imageUrl, // Alias for compatibility
    val status: ItemStatus = ItemStatus.AVAILABLE,
    val lowStockThreshold: Int = 5,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ItemStatus {
    AVAILABLE,
    PARTIALLY_RENTED,
    FULLY_RENTED,
    MAINTENANCE,
    DAMAGED
}

enum class ItemCategory {
    TENT,
    CHAIR,
    TABLE,
    DECORATION,
    LIGHTING,
    SOUND_SYSTEM,
    GENERATOR,
    CROCKERY,
    CUTLERY,
    COOKING_EQUIPMENT,
    OTHER
}