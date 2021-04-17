package com.grappim.cashier.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.grappim.cashier.data.db.entity.CategoryEntity
import com.grappim.cashier.data.db.entity.categoryEntityTableName

@Dao
interface CategoryDao : BaseDao<CategoryEntity> {

    @Query("SELECT * FROM $categoryEntityTableName")
    suspend fun getAllCategories(): List<CategoryEntity>
}