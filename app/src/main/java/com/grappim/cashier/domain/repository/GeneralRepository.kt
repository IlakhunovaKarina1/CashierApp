package com.grappim.cashier.domain.repository

import androidx.paging.PagingData
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.data.db.entity.BasketProductEntity
import com.grappim.cashier.data.db.entity.CategoryEntity
import com.grappim.cashier.data.db.entity.ProductEntity
import com.grappim.cashier.domain.acceptance.Acceptance
import com.grappim.cashier.domain.products.CreateProductUseCase
import com.grappim.cashier.ui.menu.MenuItem
import com.grappim.cashier.ui.paymentmethod.PaymentMethod
import kotlinx.coroutines.flow.Flow

interface GeneralRepository {

    suspend fun createProduct(params: CreateProductUseCase.CreateProductParams): Either<Throwable, Unit>

    suspend fun getCategories(): Either<Throwable, List<CategoryEntity>>
    suspend fun getProducts(): Either<Throwable, List<ProductEntity>>
    suspend fun getProductsByCategory(categoryEntity: CategoryEntity): List<ProductEntity>

    suspend fun clearBasket()
    suspend fun addBasketProduct(productEntity: ProductEntity)
    suspend fun removeBasketProduct(productEntity: ProductEntity)
    fun getAllBasketProducts(): Flow<List<BasketProductEntity>>

    fun getMenuItems(): Flow<List<MenuItem>>

    fun getAcceptanceListPaging(): Flow<PagingData<Acceptance>>

    suspend fun searchProducts(query: String): List<ProductEntity>

    suspend fun prePopulateDb()

    fun getProductsByQuery(
        categoryEntity: CategoryEntity?,
        query: String
    ): Flow<List<ProductEntity>>

    suspend fun getBagProducts(): List<ProductEntity>

    suspend fun deleteBagProducts()

    suspend fun makePayment(paymentMethod: PaymentMethod)

}