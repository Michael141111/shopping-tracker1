package com.inventory.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): LiveData<List<Product>>
    
    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): LiveData<List<Product>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long
    
    @Update
    suspend fun update(product: Product)
    
    @Delete
    suspend fun delete(product: Product)
}

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY purchaseDate DESC")
    fun getAllPurchases(): LiveData<List<Purchase>>
    
    @Query("SELECT * FROM purchases WHERE productId IN (SELECT id FROM products WHERE category = :category) ORDER BY purchaseDate DESC")
    fun getPurchasesByCategory(category: String): LiveData<List<Purchase>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: Purchase): Long
    
    @Update
    suspend fun update(purchase: Purchase)
    
    @Delete
    suspend fun delete(purchase: Purchase)
}
