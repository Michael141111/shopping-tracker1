package com.inventory.app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long
    
    @Update
    suspend fun update(category: Category)
    
    @Delete
    suspend fun delete(category: Category)
}

@Dao
interface UnitDao {
    @Query("SELECT * FROM units ORDER BY name ASC")
    fun getAllUnits(): LiveData<List<Unit>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: Unit): Long
    
    @Update
    suspend fun update(unit: Unit)
    
    @Delete
    suspend fun delete(unit: Unit)
}
