package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String, // Hashed
    val role: UserRole = UserRole.STAFF,
    val isActive: Boolean = true,
    val branchId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)

enum class UserRole {
    ADMIN,
    MANAGER,
    STAFF
}
