package com.inventory.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)

@Entity(tableName = "units")
data class Unit(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
