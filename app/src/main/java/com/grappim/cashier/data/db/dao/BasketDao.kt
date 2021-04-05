package com.grappim.cashier.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.grappim.cashier.data.db.entity.BasketProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface BasketDao {

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBasketProduct(basketProduct: BasketProduct)

    @Query("DELETE FROM basketproduct")
    suspend fun clearBasket()

    @Query("SELECT * FROM basketproduct WHERE uid IN (:uids)")
    suspend fun getProductsByUids(uids: List<String>): List<BasketProduct>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(basketProduct: BasketProduct): Long

    @Query("UPDATE basketproduct SET basketCount = basketCount + 1 WHERE uid=:uid")
    suspend fun incBasketProduct(uid: String)

    @Transaction
    suspend fun insertOrUpdate(basketProduct: BasketProduct) {
        val id = insert(basketProduct)
        if (id == -1L) incBasketProduct(basketProduct.uid)
    }

    @Query("DELETE FROM basketproduct WHERE uid=:uid")
    suspend fun removeProductByUid(uid: String)

    @Query("SELECT * FROM basketproduct")
    fun getAllBasketProducts(): Flow<List<BasketProduct>>

}