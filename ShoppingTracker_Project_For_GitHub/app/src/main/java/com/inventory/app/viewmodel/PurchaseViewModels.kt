package com.inventory.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.inventory.app.data.InventoryDatabase
import com.inventory.app.data.PurchaseOrder
import com.inventory.app.data.PurchaseTransaction
import com.inventory.app.repository.PurchaseOrderRepository
import com.inventory.app.repository.PurchaseTransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PurchaseOrderViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: PurchaseOrderRepository
    val allPurchaseOrders: LiveData<List<PurchaseOrder>>
    
    init {
        val purchaseOrderDao = InventoryDatabase.getDatabase(application).purchaseOrderDao()
        repository = PurchaseOrderRepository(purchaseOrderDao)
        allPurchaseOrders = repository.allPurchaseOrders
    }
    
    fun getPurchaseOrdersByStatus(status: String): LiveData<List<PurchaseOrder>> {
        return repository.getPurchaseOrdersByStatus(status)
    }
    
    fun getPurchaseOrdersByCategory(category: String): LiveData<List<PurchaseOrder>> {
        return repository.getPurchaseOrdersByCategory(category)
    }
    
    fun insert(purchaseOrder: PurchaseOrder) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(purchaseOrder)
    }
    
    fun update(purchaseOrder: PurchaseOrder) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(purchaseOrder)
    }
    
    fun delete(purchaseOrder: PurchaseOrder) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(purchaseOrder)
    }
}

class PurchaseTransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: PurchaseTransactionRepository
    val allPurchaseTransactions: LiveData<List<PurchaseTransaction>>
    
    init {
        val purchaseTransactionDao = InventoryDatabase.getDatabase(application).purchaseTransactionDao()
        repository = PurchaseTransactionRepository(purchaseTransactionDao)
        allPurchaseTransactions = repository.allPurchaseTransactions
    }
    
    fun getTransactionsForOrder(orderId: Int): LiveData<List<PurchaseTransaction>> {
        return repository.getTransactionsForOrder(orderId)
    }
    
    fun insert(purchaseTransaction: PurchaseTransaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(purchaseTransaction)
    }
    
    fun update(purchaseTransaction: PurchaseTransaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(purchaseTransaction)
    }
    
    fun delete(purchaseTransaction: PurchaseTransaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(purchaseTransaction)
    }
}
