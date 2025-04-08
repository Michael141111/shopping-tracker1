package com.inventory.app.repository

import androidx.lifecycle.LiveData
import com.inventory.app.data.InventoryAlert
import com.inventory.app.data.InventoryAlertDao
import com.inventory.app.data.InventoryItem
import com.inventory.app.data.InventoryItemDao

class InventoryItemRepository(private val inventoryItemDao: InventoryItemDao) {
    
    val allInventoryItems: LiveData<List<InventoryItem>> = inventoryItemDao.getAllInventoryItems()
    
    fun getInventoryItemsByProduct(productId: Int): LiveData<List<InventoryItem>> {
        return inventoryItemDao.getInventoryItemsByProduct(productId)
    }
    
    fun getExpiringItems(date: Long): LiveData<List<InventoryItem>> {
        return inventoryItemDao.getExpiringItems(date)
    }
    
    fun getInventorySummary(): LiveData<Map<Int, Double>> {
        return inventoryItemDao.getInventorySummary()
    }
    
    suspend fun insert(inventoryItem: InventoryItem): Long {
        return inventoryItemDao.insert(inventoryItem)
    }
    
    suspend fun update(inventoryItem: InventoryItem) {
        inventoryItemDao.update(inventoryItem)
    }
    
    suspend fun delete(inventoryItem: InventoryItem) {
        inventoryItemDao.delete(inventoryItem)
    }
}

class InventoryAlertRepository(private val inventoryAlertDao: InventoryAlertDao) {
    
    val allAlerts: LiveData<List<InventoryAlert>> = inventoryAlertDao.getAllAlerts()
    
    fun getAlertsByType(alertType: String): LiveData<List<InventoryAlert>> {
        return inventoryAlertDao.getAlertsByType(alertType)
    }
    
    fun getAlertsByProduct(productId: Int): LiveData<List<InventoryAlert>> {
        return inventoryAlertDao.getAlertsByProduct(productId)
    }
    
    suspend fun insert(inventoryAlert: InventoryAlert): Long {
        return inventoryAlertDao.insert(inventoryAlert)
    }
    
    suspend fun update(inventoryAlert: InventoryAlert) {
        inventoryAlertDao.update(inventoryAlert)
    }
    
    suspend fun delete(inventoryAlert: InventoryAlert) {
        inventoryAlertDao.delete(inventoryAlert)
    }
}
