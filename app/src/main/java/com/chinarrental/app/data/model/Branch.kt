package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val phone: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
