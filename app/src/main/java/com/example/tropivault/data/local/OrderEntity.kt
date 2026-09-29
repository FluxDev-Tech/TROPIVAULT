package com.example.tropivault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val userId: Long,
    val customerName: String,
    val customerPhone: String,
    val fulfillmentType: String, // DELIVERY, PICKUP
    val deliveryAddress: String,
    val pickupDepot: String = "",
    val paymentMethod: String, // GCASH, BANK_TRANSFER, COD, COP
    val paymentReference: String = "",
    val paymentStatus: String = "PENDING_VERIFICATION", // PENDING_VERIFICATION, VERIFIED, PAID
    val orderStatus: String = "PLACED", // PLACED, PROCESSING, READY_FOR_DISPATCH, IN_TRANSIT, DELIVERED, COMPLETED
    val subtotal: Double,
    val deliveryFee: Double,
    val totalAmount: Double,
    val riderId: Long? = null,
    val riderName: String? = null,
    val proofNotes: String? = null,
    val codCollected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
