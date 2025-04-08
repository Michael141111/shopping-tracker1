package com.inventory.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchase_orders",
    indices = [Index("productId")]
)
data class PurchaseOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val quantity: Double,
    val requestDate: Long,
    val status: String // "pending", "completed", "cancelled"
)

@Entity(
    tableName = "purchase_transactions",
    foreignKeys = [
        ForeignKey(
            entity = PurchaseOrder::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderId")]
)
data class PurchaseTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Int,
    val purchaseDate: Long,
    val quantity: Double,
    val expiryDate: Long?
)
