package com.inventory.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.inventory.app.data.InventoryDatabase
import com.inventory.app.data.Product
import com.inventory.app.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>
    
    init {
        val productDao = InventoryDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.allProducts
    }
    
    fun getProductsByCategory(category: String): LiveData<List<Product>> {
        return repository.getProductsByCategory(category)
    }
    
    fun insert(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(product)
    }
    
    fun update(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(product)
    }
    
    fun delete(product: Product) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(product)
    }
}
