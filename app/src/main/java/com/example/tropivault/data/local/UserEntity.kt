package com.example.tropivault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val password: String = "password123",
    val fullName: String,
    val role: String, // CLIENT, FARMER, RIDER, ADMIN
    val phone: String = "",
    val address: String = "",
    val farmName: String = "",
    val farmLocation: String = "",
    val vehicleType: String = "", // e.g. Motorcycle (Insulated), Eco Van, Cargo Trike
    val licenseNumber: String = "",
    val status: String = "APPROVED", // APPROVED, PENDING, REJECTED
    val createdAt: Long = System.currentTimeMillis()
)
