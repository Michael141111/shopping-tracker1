package com.inventory.app.repository

import androidx.lifecycle.LiveData
import com.inventory.app.data.Product
import com.inventory.app.data.ProductDao

class ProductRepository(private val productDao: ProductDao) {
    
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()
    
    fun getProductsByCategory(category: String): LiveData<List<Product>> {
        return productDao.getProductsByCategory(category)
    }
    
    suspend fun insert(product: Product): Long {
        return productDao.insert(product)
    }
    
    suspend fun update(product: Product) {
        productDao.update(product)
    }
    
    suspend fun delete(product: Product) {
        productDao.delete(product)
    }
}
