package com.grappim.cashier.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.data.db.converter.BaseListsConverter
import com.grappim.cashier.data.db.converter.BigDecimalConverter
import com.grappim.cashier.data.db.dao.BasketDao
import com.grappim.cashier.data.db.dao.CategoryDao
import com.grappim.cashier.data.db.dao.ProductsDao
import com.grappim.cashier.data.db.entity.BasketProduct

@Database(
    entities = [
        Product::class,
        Category::class,
        BasketProduct::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    BigDecimalConverter::class,
    BaseListsConverter::class
)
abstract class CashierDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDao
    abstract fun basketDao(): BasketDao
    abstract fun categoryDao(): CategoryDao
}