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
import com.grappim.cashier.data.db.entity.AcceptanceDB
import com.grappim.cashier.data.db.entity.BasketProduct
import com.grappim.cashier.data.db.entity.Category
import com.grappim.cashier.data.db.entity.Product
import com.grappim.cashier.domain.acceptance.Acceptance
import com.grappim.cashier.domain.acceptance.Mapper.toDomain
import com.grappim.cashier.domain.cashier.Cashier
import com.grappim.cashier.domain.login.LoginRequest
import com.grappim.cashier.domain.login.LoginUseCase
import com.grappim.cashier.domain.outlet.Mapper.toDomain
import com.grappim.cashier.domain.outlet.Outlet
import com.grappim.cashier.ui.acceptance.AcceptanceStatus
import com.grappim.cashier.ui.menu.MenuItem
import com.grappim.cashier.ui.menu.MenuItemType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    suspend fun getOutlets(): Either<Throwable, List<Outlet>>
    suspend fun getCashiers(): Either<Throwable, List<Cashier>>

    suspend fun getCategories(): Either<Throwable, List<Category>>
    suspend fun getProducts(): Either<Throwable, List<Product>>
    suspend fun getProductsByCategory(category: Category): List<Product>

    suspend fun clearBasket()
    suspend fun addBasketProduct(product: Product)
    suspend fun removeBasketProduct(product: Product)
    fun getAllBasketProducts(): Flow<List<BasketProduct>>

    fun getMenuItems(): Flow<List<MenuItem>>

    fun getAcceptanceListPaging(): Flow<PagingData<Acceptance>>

    suspend fun saveCashier(cashier: Cashier)
    suspend fun saveOutlet(outlet: Outlet)

    suspend fun searchProducts(query: String): List<Product>

    suspend fun prePopulateDb()

    fun getProductsByQuery(
        category: Category?,
        query: String
    ): Flow<List<Product>>

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

    override suspend fun getOutlets(): Either<Throwable, List<Outlet>> =
        apiCall {
            val merchantId = generalStorage.getMerchantId()
            cashierApi.getStocks(merchantId)
        }.map {
            withContext(Dispatchers.IO) {
                it.stocks.toDomain()
            }
        }

    override suspend fun getCashiers(): Either<Throwable, List<Cashier>> =
        Either.Right(getCashierList())

    override suspend fun getCategories(): Either<Throwable, List<Category>> =
        Either.Right(getCategoryList())

    override suspend fun getProducts(): Either<Throwable, List<Product>> =
        Either.Right(getProductList())

    override suspend fun getProductsByCategory(category: Category): List<Product> =
        withContext(Dispatchers.IO) {
            if (category.isDefault) {
                productsDao.getAllProducts()
            } else {
                productsDao.searchProductsByCategoryId(category.uid)
            }
        }

    override suspend fun clearBasket() =
        basketDao.clearBasket()

    override suspend fun addBasketProduct(product: Product) {
        basketDao.insertOrUpdate(product.toBasketProduct())
    }

    override suspend fun removeBasketProduct(product: Product) {
        if (product.basketCount <= bigDecimalZero()) {
            basketDao.removeProductByUid(product.uid)
        } else {
            basketDao.updateBasketProduct(product.toBasketProduct())
        }
    }

    override fun getAllBasketProducts(): Flow<List<BasketProduct>> =
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

    override suspend fun saveCashier(cashier: Cashier) = withContext(Dispatchers.IO) {
        generalStorage.setCashierInfo(cashier)
        delay(100)
    }

    override suspend fun saveOutlet(outlet: Outlet) = withContext(Dispatchers.IO) {
        generalStorage.setOutletInfo(outlet)
    }

    override fun getProductsByQuery(category: Category?, query: String): Flow<List<Product>> {
        val roomQuery = StringBuilder("SELECT * FROM product ")
            .append(
                if (query.isNotBlank() || (category != null && !category.isDefault)
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
                if (category == null || category.isDefault) {
                    ""
                } else {
                    val andQuery = if (query.isBlank()) {
                        ""
                    } else {
                        "AND "
                    }
                    "${andQuery}categoryId = ${category.uid}"
                }
            )
        return productsDao.getProductsFlow(
            SimpleSQLiteQuery(roomQuery.toString())
        )
    }

    override suspend fun searchProducts(query: String): List<Product> =
        withContext(Dispatchers.IO) {
            val products = productsDao.searchProducts(query.getStringForDbQuery())

            val productsUids = products.map { it.uid }
            val storedBasketProducts = basketDao.getProductsByUids(productsUids)

            return@withContext if (storedBasketProducts.isEmpty()) {
                products
            } else {
                val resultList: List<Product> = products
                resultList.forEach { product ->
                    storedBasketProducts.forEach { storedProduct ->
                        if (storedProduct.uid == product.uid) {
                            product.basketCount = storedProduct.basketCount
                        }
                    }
                }
                resultList
            }
        }

    override suspend fun prePopulateDb() = withContext(Dispatchers.IO) {
        val products = mutableListOf<Product>()
        (0..20).forEach {
            products.add(
                Product(
                    uid = "$it",
                    name = "Product $it",
                    imageUrl = "",
                    price = BigDecimal("${Random.nextInt(50, 400)}"),
                    stockCount = BigDecimal("${Random.nextInt(0, 10)}"),
                    categoryId = Random.nextInt(2, 8).toString()
                )
            )
        }
        productsDao.insert(products)

        val categories = mutableListOf<Category>()
        categories.add(
            Category(
                uid = "0",
                name = "All",
                isDefault = true
            )
        )
        (1..11).forEach {
            categories.add(
                Category(
                    uid = "$it",
                    name = "Category $it"
                )
            )
        }
        categoryDao.insert(categories)

        val list = mutableListOf<AcceptanceDB>()
        (0..20).forEach {
            val randomDateInstant = getRandomDate()
            val dateString = randomDateInstant.atOffset(DateTimeUtils.getZoneOffset(false))
            list.add(
                AcceptanceDB(
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

    private fun getCashierList(): List<Cashier> {
        val cashiers = mutableListOf<Cashier>()
        (0..10).forEach {
            cashiers.add(Cashier(id = "$it", name = "Cashier $it"))
        }
        return cashiers.toList()
    }

    private suspend fun getCategoryList(): List<Category> = categoryDao.getAllCategories()

    private suspend fun getProductList(): List<Product> = withContext(Dispatchers.IO) {
        val products = productsDao.getAllProducts()

        val productsUids = products.map { it.uid }
        val storedBasketProducts = basketDao.getProductsByUids(productsUids)

        return@withContext if (storedBasketProducts.isEmpty()) {
            products
        } else {
            val resultList: List<Product> = products
            resultList.forEach { product ->
                storedBasketProducts.forEach { storedProduct ->
                    if (storedProduct.uid == product.uid) {
                        product.basketCount = storedProduct.basketCount
                    }
                }
            }
            resultList
        }
    }

    fun Product.toBasketProduct(): BasketProduct =
        BasketProduct(
            uid = this.uid,
            name = this.name,
            basketCount = this.basketCount,
            imageUrl = this.imageUrl,
            price = this.price,
            categoryId = this.categoryId,
            stockCount = this.stockCount
        )

}