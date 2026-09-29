package com.example.tropivault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val unit: String,
    val farmName: String
)
