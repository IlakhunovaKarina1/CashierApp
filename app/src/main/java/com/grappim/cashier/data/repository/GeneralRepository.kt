package com.grappim.cashier.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.sqlite.db.SimpleSQLiteQuery
import com.grappim.cashier.R
import com.grappim.cashier.api.CashierApi
import com.grappim.cashier.core.extensions.bigDecimalZero
import com.grappim.cashier.core.extensions.getEpochMilli
import com.grappim.cashier.core.extensions.getStringForDbQuery
import com.grappim.cashier.core.functional.Either
import com.grappim.cashier.core.functional.map
import com.grappim.cashier.core.storage.GeneralStorage
import com.grappim.cashier.core.utils.DateTimeUtils
import com.grappim.cashier.data.db.dao.AcceptanceDao
import com.grappim.cashier.data.db.dao.BasketDao
import com.grappim.cashier.data.db.dao.CategoryDao
import com.grappim.cashier.data.db.dao.ProductsDao
import com.grappim.cashier.data.db.entity.AcceptanceEntity
import com.grappim.cashier.data.db.entity.BasketProductEntity
import com.grappim.cashier.data.db.entity.CategoryEntity
import com.grappim.cashier.data.db.entity.ProductEntity
import com.grappim.cashier.data.db.entity.productEntityTableName
import com.grappim.cashier.domain.acceptance.Acceptance
import com.grappim.cashier.domain.acceptance.Mapper.toDomain
import com.grappim.cashier.domain.login.LoginRequest
import com.grappim.cashier.domain.login.LoginUseCase
import com.grappim.cashier.ui.acceptance.AcceptanceStatus
import com.grappim.cashier.ui.menu.MenuItem
import com.grappim.cashier.ui.menu.MenuItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

interface GeneralRepository {

    suspend fun login(loginRequestData: LoginUseCase.LoginRequestData): Either<Throwable, Unit>

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

}

@Singleton
class GeneralRepositoryImpl @Inject constructor(
    private val cashierApi: CashierApi,
    private val basketDao: BasketDao,
    private val productsDao: ProductsDao,
    private val generalStorage: GeneralStorage,
    private val categoryDao: CategoryDao,
    private val acceptanceDao: AcceptanceDao
) : GeneralRepository, BaseRepository() {

    override suspend fun login(loginRequestData: LoginUseCase.LoginRequestData): Either<Throwable, Unit> =
        apiCall {
            cashierApi.login(
                LoginRequest(
                    mobile = loginRequestData.mobile,
                    password = loginRequestData.password
                )
            )
        }.map {
            generalStorage.setMerchantId(it.merchantId)
            generalStorage.setAuthToken(it.token)
        }

    override suspend fun getCategories(): Either<Throwable, List<CategoryEntity>> =
        Either.Right(getCategoryList())

    override suspend fun getProducts(): Either<Throwable, List<ProductEntity>> =
        Either.Right(getProductList())

    override suspend fun getProductsByCategory(categoryEntity: CategoryEntity): List<ProductEntity> =
        withContext(Dispatchers.IO) {
            if (categoryEntity.isDefault) {
                productsDao.getAllProducts()
            } else {
                productsDao.searchProductsByCategoryId(categoryEntity.uid)
            }
        }

    override suspend fun clearBasket() =
        basketDao.clearBasket()

    override suspend fun addBasketProduct(productEntity: ProductEntity) {
        basketDao.insertOrUpdate(productEntity.toBasketProduct())
    }

    override suspend fun removeBasketProduct(productEntity: ProductEntity) {
        if (productEntity.basketCount <= bigDecimalZero()) {
            basketDao.removeProductByUid(productEntity.id)
        } else {
            basketDao.updateBasketProduct(productEntity.toBasketProduct())
        }
    }

    override fun getAllBasketProducts(): Flow<List<BasketProductEntity>> =
        basketDao.getAllBasketProducts()

    override fun getMenuItems(): Flow<List<MenuItem>> = flow {
        emit(
            listOf(
                MenuItem(
                    type = MenuItemType.SALES,
                    text = R.string.title_sales,
                    image = R.drawable.ic_cash_register
                ),
                MenuItem(
                    type = MenuItemType.PRODUCTS,
                    text = R.string.title_products,
                    image = R.drawable.ic_store
                ),
                MenuItem(
                    type = MenuItemType.ACCEPTANCE,
                    text = R.string.tittle_acceptance,
                    image = R.drawable.ic_store_acceptance
                )
            )
        )
    }

    override fun getAcceptanceListPaging(): Flow<PagingData<Acceptance>> =
        Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = false,
                initialLoadSize = 10
            )
        ) {
            acceptanceDao.getAcceptanceListPaging()
        }.flow.map {
            it.map { acceptanceDb ->
                acceptanceDb.toDomain()
            }
        }

    private fun getRandomAcceptanceStatus(): AcceptanceStatus = listOf(
        AcceptanceStatus.PAID,
        AcceptanceStatus.STANDARD
    ).random()

    override fun getProductsByQuery(
        categoryEntity: CategoryEntity?,
        query: String
    ): Flow<List<ProductEntity>> {
        val roomQuery = StringBuilder("SELECT * FROM $productEntityTableName ")
            .append(
                if (query.isNotBlank() || (categoryEntity != null && !categoryEntity.isDefault)
                ) {
                    "WHERE "
                } else {
                    ""
                }
            )
            .append(
                if (query.isNotBlank()) {
                    "name LIKE '${query.getStringForDbQuery()}' "
                } else {
                    ""
                }
            )
            .append(
                if (categoryEntity == null || categoryEntity.isDefault) {
                    ""
                } else {
                    val andQuery = if (query.isBlank()) {
                        ""
                    } else {
                        "AND "
                    }
                    "${andQuery}categoryId = ${categoryEntity.uid}"
                }
            )
        return productsDao.getProductsFlow(
            SimpleSQLiteQuery(roomQuery.toString())
        )
    }

    override suspend fun searchProducts(query: String): List<ProductEntity> =
        withContext(Dispatchers.IO) {
            val products = productsDao.searchProducts(query.getStringForDbQuery())

            val productsUids = products.map { it.id }
            val storedBasketProducts = basketDao.getProductsByUids(productsUids)

            return@withContext if (storedBasketProducts.isEmpty()) {
                products
            } else {
                val resultList: List<ProductEntity> = products
                resultList.forEach { product ->
                    storedBasketProducts.forEach { storedProduct ->
                        if (storedProduct.id == product.id) {
                            product.basketCount = storedProduct.basketCount
                        }
                    }
                }
                resultList
            }
        }

    override suspend fun prePopulateDb() = withContext(Dispatchers.IO) {
        val products = mutableListOf<ProductEntity>()
        (0..20).forEach {
            products.add(
                ProductEntity(
                    id = it.toLong(),
                    name = "Product $it",
                    imageUrl = "",
                    price = BigDecimal("${Random.nextInt(50, 400)}"),
                    stockCount = BigDecimal("${Random.nextInt(0, 10)}"),
                    categoryId = Random.nextInt(2, 8).toString(),
                    barcode = "barcode $it"
                )
            )
        }
        productsDao.insert(products)

        val categories = mutableListOf<CategoryEntity>()
        categories.add(
            CategoryEntity(
                uid = "0",
                name = "All",
                isDefault = true
            )
        )
        (1..11).forEach {
            categories.add(
                CategoryEntity(
                    uid = "$it",
                    name = "Category $it"
                )
            )
        }
        categoryDao.insert(categories)

        val list = mutableListOf<AcceptanceEntity>()
        (0..20).forEach {
            val randomDateInstant = getRandomDate()
            val dateString = randomDateInstant.atOffset(DateTimeUtils.getZoneOffset(false))
            list.add(
                AcceptanceEntity(
                    id = "$it",
                    vendorName = "Vendor Name $it",
                    date = DateTimeUtils.getDateTimePatternStandard().format(dateString),
                    dateMillis = randomDateInstant.toEpochMilli(),
                    status = getRandomAcceptanceStatus(),
                    toPay = BigDecimal("${Random.nextInt(50, 400)}"),
                    paidSum = BigDecimal("${Random.nextInt(50, 400)}")
                )
            )
        }
        acceptanceDao.insert(list)
    }

    private fun getRandomDate(): Instant {
        val start: Long = DateTimeUtils.getNowOffsetDateTime().minusWeeks(1).getEpochMilli()
        val end: Long = DateTimeUtils.getNowOffsetDateTime().getEpochMilli()
        val randomTime: Long = ThreadLocalRandom
            .current()
            .nextLong(start, end)
        return Instant.ofEpochMilli(randomTime)
    }

    private suspend fun getCategoryList(): List<CategoryEntity> = categoryDao.getAllCategories()

    private suspend fun getProductList(): List<ProductEntity> = withContext(Dispatchers.IO) {
        val products = productsDao.getAllProducts()

        val productsUids = products.map { it.id }
        val storedBasketProducts = basketDao.getProductsByUids(productsUids)

        return@withContext if (storedBasketProducts.isEmpty()) {
            products
        } else {
            val resultList: List<ProductEntity> = products
            resultList.forEach { product ->
                storedBasketProducts.forEach { storedProduct ->
                    if (storedProduct.id == product.id) {
                        product.basketCount = storedProduct.basketCount
                    }
                }
            }
            resultList
        }
    }

    fun ProductEntity.toBasketProduct(): BasketProductEntity =
        BasketProductEntity(
            id = this.id,
            name = this.name,
            basketCount = this.basketCount,
            imageUrl = this.imageUrl,
            price = this.price,
            categoryId = this.categoryId,
            stockCount = this.stockCount,
            barcode = this.barcode
        )

}