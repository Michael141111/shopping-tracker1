package com.inventory.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val quantity: Double,
    val purchaseDate: Long,
    val expiryDate: Long?
)

@Entity(tableName = "inventory_alerts")
data class InventoryAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val alertType: String, // "low_stock", "expiring_soon"
    val threshold: Double?, // For low_stock alerts
    val createdDate: Long
)
