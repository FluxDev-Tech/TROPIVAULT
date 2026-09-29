package com.example.tropivault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0, // 0 for broadcast / role-based
    val targetRole: String = "ALL", // ALL, CLIENT, FARMER, RIDER, ADMIN
    val title: String,
    val message: String,
    val type: String = "GENERAL", // ORDER, HARVEST, PAYMENT, STORAGE_ALERT, APPROVAL
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
