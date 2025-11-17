package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val rentPerDay: Double,
    val quantity: Int,
    val availableQuantity: Int,
    val description: String = "",
    val imageUrl: String = "",
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
