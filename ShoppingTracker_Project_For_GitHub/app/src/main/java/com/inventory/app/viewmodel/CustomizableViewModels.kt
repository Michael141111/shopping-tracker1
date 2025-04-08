package com.inventory.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.inventory.app.data.Category
import com.inventory.app.data.InventoryDatabase
import com.inventory.app.data.Unit
import com.inventory.app.repository.CategoryRepository
import com.inventory.app.repository.UnitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: CategoryRepository
    val allCategories: LiveData<List<Category>>
    
    init {
        val categoryDao = InventoryDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        allCategories = repository.allCategories
    }
    
    fun insert(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(category)
    }
    
    fun update(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(category)
    }
    
    fun delete(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(category)
    }
}

class UnitViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: UnitRepository
    val allUnits: LiveData<List<Unit>>
    
    init {
        val unitDao = InventoryDatabase.getDatabase(application).unitDao()
        repository = UnitRepository(unitDao)
        allUnits = repository.allUnits
    }
    
    fun insert(unit: Unit) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(unit)
    }
    
    fun update(unit: Unit) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(unit)
    }
    
    fun delete(unit: Unit) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(unit)
    }
}
