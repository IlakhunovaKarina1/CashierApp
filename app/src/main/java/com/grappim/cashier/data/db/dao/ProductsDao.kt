package com.grappim.cashier.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.grappim.cashier.data.db.entity.Product

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(products: List<Product>)

    @Query("DELETE FROM product")
    suspend fun clearProducts()

    @Query("SELECT * FROM product")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM product WHERE name LIKE :query")
    suspend fun searchProducts(query: String): List<Product>

    @Query("SELECT * FROM product WHERE categoryId=:categoryId")
    suspend fun searchProductsByCategoryId(categoryId: String): List<Product>

}