package com.example.tropivault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val price: Double,
    val unit: String,
    val quantity: Int,
    val farmName: String,
    val imageUrl: String = ""
)
