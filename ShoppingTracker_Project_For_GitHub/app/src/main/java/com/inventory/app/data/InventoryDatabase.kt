package com.inventory.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class, Purchase::class, Category::class, Unit::class, PurchaseOrder::class, PurchaseTransaction::class, InventoryItem::class, InventoryAlert::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun unitDao(): UnitDao
    abstract fun purchaseOrderDao(): PurchaseOrderDao
    abstract fun purchaseTransactionDao(): PurchaseTransactionDao
    abstract fun inventoryItemDao(): InventoryItemDao
    abstract fun inventoryAlertDao(): InventoryAlertDao
    
    companion object {
        @Volatile
        private var INSTANCE: InventoryDatabase? = null
        
        fun getDatabase(context: Context): InventoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDatabase::class.java,
                    "inventory_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
