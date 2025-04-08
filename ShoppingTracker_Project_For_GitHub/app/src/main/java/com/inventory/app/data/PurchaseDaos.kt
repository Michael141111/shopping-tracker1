package com.inventory.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PurchaseOrderDao {
    @Query("SELECT * FROM purchase_orders ORDER BY requestDate DESC")
    fun getAllPurchaseOrders(): LiveData<List<PurchaseOrder>>
    
    @Query("SELECT * FROM purchase_orders WHERE status = :status ORDER BY requestDate DESC")
    fun getPurchaseOrdersByStatus(status: String): LiveData<List<PurchaseOrder>>
    
    @Query("SELECT * FROM purchase_orders WHERE productId IN (SELECT id FROM products WHERE category = :category) ORDER BY requestDate DESC")
    fun getPurchaseOrdersByCategory(category: String): LiveData<List<PurchaseOrder>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchaseOrder: PurchaseOrder): Long
    
    @Update
    suspend fun update(purchaseOrder: PurchaseOrder)
    
    @Delete
    suspend fun delete(purchaseOrder: PurchaseOrder)
}

@Dao
interface PurchaseTransactionDao {
    @Query("SELECT * FROM purchase_transactions ORDER BY purchaseDate DESC")
    fun getAllPurchaseTransactions(): LiveData<List<PurchaseTransaction>>
    
    @Query("SELECT * FROM purchase_transactions WHERE orderId = :orderId")
    fun getTransactionsForOrder(orderId: Int): LiveData<List<PurchaseTransaction>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchaseTransaction: PurchaseTransaction): Long
    
    @Update
    suspend fun update(purchaseTransaction: PurchaseTransaction)
    
    @Delete
    suspend fun delete(purchaseTransaction: PurchaseTransaction)
}
