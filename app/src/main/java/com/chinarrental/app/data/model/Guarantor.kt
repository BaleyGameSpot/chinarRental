package com.chinarrental.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "guarantors",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class Guarantor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val name: String,
    val phone: String,
    val phone2: String = "",
    val address: String = "",
    val cnicNumber: String = "",
    val cnicPhotoFront: String = "",
    val cnicPhotoBack: String = "",
    val relationship: String = "",
    val createdAt: Long = System.currentTimeMillis()
)