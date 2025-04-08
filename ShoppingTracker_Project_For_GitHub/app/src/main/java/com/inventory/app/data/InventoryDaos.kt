package com.inventory.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface InventoryItemDao {
    @Query("SELECT * FROM inventory_items ORDER BY expiryDate ASC NULLS LAST")
    fun getAllInventoryItems(): LiveData<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE productId = :productId ORDER BY expiryDate ASC NULLS LAST")
    fun getInventoryItemsByProduct(productId: Int): LiveData<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE expiryDate IS NOT NULL AND expiryDate <= :date ORDER BY expiryDate ASC")
    fun getExpiringItems(date: Long): LiveData<List<InventoryItem>>
    
    @Query("SELECT productId, SUM(quantity) as totalQuantity FROM inventory_items GROUP BY productId")
    fun getInventorySummary(): LiveData<Map<Int, Double>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inventoryItem: InventoryItem): Long
    
    @Update
    suspend fun update(inventoryItem: InventoryItem)
    
    @Delete
    suspend fun delete(inventoryItem: InventoryItem)
}

@Dao
interface InventoryAlertDao {
    @Query("SELECT * FROM inventory_alerts ORDER BY createdDate DESC")
    fun getAllAlerts(): LiveData<List<InventoryAlert>>
    
    @Query("SELECT * FROM inventory_alerts WHERE alertType = :alertType ORDER BY createdDate DESC")
    fun getAlertsByType(alertType: String): LiveData<List<InventoryAlert>>
    
    @Query("SELECT * FROM inventory_alerts WHERE productId = :productId ORDER BY createdDate DESC")
    fun getAlertsByProduct(productId: Int): LiveData<List<InventoryAlert>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inventoryAlert: InventoryAlert): Long
    
    @Update
    suspend fun update(inventoryAlert: InventoryAlert)
    
    @Delete
    suspend fun delete(inventoryAlert: InventoryAlert)
}
