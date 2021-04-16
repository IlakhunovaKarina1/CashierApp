package com.grappim.cashier.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grappim.cashier.data.db.entity.CategoryEntity
import com.grappim.cashier.data.db.entity.categoryEntityTableName

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryEntities: List<CategoryEntity>)

    @Query("SELECT * FROM $categoryEntityTableName")
    suspend fun getAllCategories(): List<CategoryEntity>
}