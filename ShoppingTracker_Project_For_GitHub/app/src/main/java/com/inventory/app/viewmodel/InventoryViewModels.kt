package com.inventory.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.inventory.app.data.InventoryAlert
import com.inventory.app.data.InventoryDatabase
import com.inventory.app.data.InventoryItem
import com.inventory.app.repository.InventoryAlertRepository
import com.inventory.app.repository.InventoryItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryItemViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: InventoryItemRepository
    val allInventoryItems: LiveData<List<InventoryItem>>
    
    init {
        val inventoryItemDao = InventoryDatabase.getDatabase(application).inventoryItemDao()
        repository = InventoryItemRepository(inventoryItemDao)
        allInventoryItems = repository.allInventoryItems
    }
    
    fun getInventoryItemsByProduct(productId: Int): LiveData<List<InventoryItem>> {
        return repository.getInventoryItemsByProduct(productId)
    }
    
    fun getExpiringItems(date: Long): LiveData<List<InventoryItem>> {
        return repository.getExpiringItems(date)
    }
    
    fun getInventorySummary(): LiveData<Map<Int, Double>> {
        return repository.getInventorySummary()
    }
    
    fun insert(inventoryItem: InventoryItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(inventoryItem)
    }
    
    fun update(inventoryItem: InventoryItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(inventoryItem)
    }
    
    fun delete(inventoryItem: InventoryItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(inventoryItem)
    }
}

class InventoryAlertViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: InventoryAlertRepository
    val allAlerts: LiveData<List<InventoryAlert>>
    
    init {
        val inventoryAlertDao = InventoryDatabase.getDatabase(application).inventoryAlertDao()
        repository = InventoryAlertRepository(inventoryAlertDao)
        allAlerts = repository.allAlerts
    }
    
    fun getAlertsByType(alertType: String): LiveData<List<InventoryAlert>> {
        return repository.getAlertsByType(alertType)
    }
    
    fun getAlertsByProduct(productId: Int): LiveData<List<InventoryAlert>> {
        return repository.getAlertsByProduct(productId)
    }
    
    fun insert(inventoryAlert: InventoryAlert) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(inventoryAlert)
    }
    
    fun update(inventoryAlert: InventoryAlert) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(inventoryAlert)
    }
    
    fun delete(inventoryAlert: InventoryAlert) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(inventoryAlert)
    }
}
