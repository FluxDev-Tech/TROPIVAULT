package com.example.tropivault.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Mangoes, Bananas, Citrus & Melons, Exotic & Rare, Preserved & Dried, Farm Bundles
    val description: String,
    val price: Double,
    val unit: String = "per kg",
    val stockKg: Double,
    val farmId: Long = 1,
    val farmName: String,
    val location: String,
    val harvestDate: String,
    val preservationNotes: String, // e.g. "Cold-stored at 12°C in Nitrogen vault"
    val storageTemp: String = "12°C",
    val shelfLifeDaysRemaining: Int = 14,
    val preservationGrade: String = "Vault Grade A+",
    val isApproved: Boolean = true,
    val isFeatured: Boolean = false,
    val rating: Float = 4.8f,
    val imageUrl: String = ""
)
