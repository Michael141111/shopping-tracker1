package com.inventory.app.repository

import androidx.lifecycle.LiveData
import com.inventory.app.data.Category
import com.inventory.app.data.CategoryDao
import com.inventory.app.data.Unit
import com.inventory.app.data.UnitDao

class CategoryRepository(private val categoryDao: CategoryDao) {
    
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()
    
    suspend fun insert(category: Category): Long {
        return categoryDao.insert(category)
    }
    
    suspend fun update(category: Category) {
        categoryDao.update(category)
    }
    
    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }
}

class UnitRepository(private val unitDao: UnitDao) {
    
    val allUnits: LiveData<List<Unit>> = unitDao.getAllUnits()
    
    suspend fun insert(unit: Unit): Long {
        return unitDao.insert(unit)
    }
    
    suspend fun update(unit: Unit) {
        unitDao.update(unit)
    }
    
    suspend fun delete(unit: Unit) {
        unitDao.delete(unit)
    }
}
