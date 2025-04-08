package com.inventory.app.repository

import androidx.lifecycle.LiveData
import com.inventory.app.data.PurchaseOrder
import com.inventory.app.data.PurchaseOrderDao
import com.inventory.app.data.PurchaseTransaction
import com.inventory.app.data.PurchaseTransactionDao

class PurchaseOrderRepository(private val purchaseOrderDao: PurchaseOrderDao) {
    
    val allPurchaseOrders: LiveData<List<PurchaseOrder>> = purchaseOrderDao.getAllPurchaseOrders()
    
    fun getPurchaseOrdersByStatus(status: String): LiveData<List<PurchaseOrder>> {
        return purchaseOrderDao.getPurchaseOrdersByStatus(status)
    }
    
    fun getPurchaseOrdersByCategory(category: String): LiveData<List<PurchaseOrder>> {
        return purchaseOrderDao.getPurchaseOrdersByCategory(category)
    }
    
    suspend fun insert(purchaseOrder: PurchaseOrder): Long {
        return purchaseOrderDao.insert(purchaseOrder)
    }
    
    suspend fun update(purchaseOrder: PurchaseOrder) {
        purchaseOrderDao.update(purchaseOrder)
    }
    
    suspend fun delete(purchaseOrder: PurchaseOrder) {
        purchaseOrderDao.delete(purchaseOrder)
    }
}

class PurchaseTransactionRepository(private val purchaseTransactionDao: PurchaseTransactionDao) {
    
    val allPurchaseTransactions: LiveData<List<PurchaseTransaction>> = purchaseTransactionDao.getAllPurchaseTransactions()
    
    fun getTransactionsForOrder(orderId: Int): LiveData<List<PurchaseTransaction>> {
        return purchaseTransactionDao.getTransactionsForOrder(orderId)
    }
    
    suspend fun insert(purchaseTransaction: PurchaseTransaction): Long {
        return purchaseTransactionDao.insert(purchaseTransaction)
    }
    
    suspend fun update(purchaseTransaction: PurchaseTransaction) {
        purchaseTransactionDao.update(purchaseTransaction)
    }
    
    suspend fun delete(purchaseTransaction: PurchaseTransaction) {
        purchaseTransactionDao.delete(purchaseTransaction)
    }
}
